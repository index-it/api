package app.index.api.routing.list.routes

import app.index.api.plugins.AuthenticationMethods
import app.index.api.plugins.emitWebsocketEvent
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.usecases.ListInvitationUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.list.ListDao
import app.index.data.daos.list.ListInvitationDao
import app.index.data.daos.user.UserDao
import app.index.data.models.lists.ListData
import io.github.smiley4.ktorswaggerui.dsl.resources.delete
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.listPermissionRoute() {
    val listDao by inject<ListDao>()
    val userDao by inject<UserDao>()
    val listInvitationUseCase by inject<ListInvitationUseCase>()
    val listInvitationDao by inject<ListInvitationDao>()
    val websocketEventManager by inject<WebsocketEventManager>()


    authenticate(AuthenticationMethods.USER_SESSION_AUTH) {

        post<ListsRoute.ListRoute.PermissionsRoute>({
            tags = listOf("lists")
            operationId = "add-user"
            summary = "invites a user to have access to a list or changes his permissions if he was already added"
            request {
                pathParameter<String>("list_id") {
                    required = true
                    description = "the id of the list"
                }
                body<ListData.ListPermissionAddRequestData> {
                    description =
                        "the user to invite and whether to grant him edit permission, otherwise if editor is set to false he will be invited as a viewer"
                    required = true
                    example(
                        "sample-user-permission-update", ListData.ListPermissionAddRequestData(
                            email = "j@index-it.app",
                            editor = false
                        )
                    )
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "user already had permissions and they were updated successfully"
                    body<ListData> {
                        description = "the updated list"
                    }
                }
                HttpStatusCode.Created to {
                    description = "user invited successfully"
                }
                HttpStatusCode.BadRequest to {
                    description = "you cannot invite yourself to a list"
                }
                HttpStatusCode.NotFound to {
                    description = "list not found"
                }
            }
        }) {
            val userId = userIdFromSessionOrThrow()
            val listId = it.parent.list_id

            val inviter = userDao.get(userId)
                ?: return@post call.respond(HttpStatusCode.NotFound)
            val permissionInfo = call.receive<ListData.ListPermissionAddRequestData>()
            val invitedUser = userDao.getFromEmail(permissionInfo.email)

            if (inviter.email == invitedUser?.email) {
                return@post call.respond(HttpStatusCode.BadRequest, "you cannot invite yourself to a list")
            }

            val list = listDao.get(userId, listId)
                ?: return@post call.respond(HttpStatusCode.NotFound)

            // if the user is null it means it doesn't have an index account, so he'll need to create one after accepting the invitation
            val addAsViewer =
                invitedUser == null || (!permissionInfo.editor && list.viewers.none { user -> user == invitedUser.id })
            val addAsEditor =
                invitedUser == null || (permissionInfo.editor && list.editors.none { user -> user == invitedUser.id })
            val hasAlreadyAcceptedInvitation =
                invitedUser != null && (list.editors.any { user -> user == invitedUser.id } || list.editors.any { user -> user == invitedUser.id })

            if (!addAsViewer && !addAsEditor) {
                // user is already added, no need to perform any action
                call.respond(list)
            } else if (hasAlreadyAcceptedInvitation) {
                // user has already accepted the invitation, just update the permission
                val updatedList = if (permissionInfo.editor) {
                    listDao.addPermissionToUser(userId, listId, invitedUser!!.id, true)
                } else {
                    listDao.addPermissionToUser(userId, listId, invitedUser!!.id, false)
                }

                if (updatedList == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(updatedList)

                    emitWebsocketEvent(
                        websocketEventManager = websocketEventManager,
                        type = WebsocketEventType.LIST_UPDATED,
                        content = WebsocketEventContent.ListCreateOrUpdateEventContent(updatedList)
                    )
                }
            } else {
                val sent = listInvitationUseCase.sendInvitation(
                    fromUserEmail = inviter.email,
                    listId = listId,
                    listName = list.name,
                    toUserEmail = permissionInfo.email,
                    editor = addAsEditor
                )

                if (sent) {
                    call.respond(HttpStatusCode.Created)
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }

        delete<ListsRoute.ListRoute.PermissionsRoute>({
            tags = listOf("lists")
            operationId = "remove-user"
            summary = "removes access to a user from the list"
            request {
                pathParameter<String>("list_id") {
                    required = true
                    description = "the id of the list"
                }
                body<ListData.ListPermissionRemoveRequestData> {
                    description = "the user to remove from the list"
                    required = true
                    example(
                        "sample-user-permission-remove", ListData.ListPermissionRemoveRequestData(
                            user_id = IxId(UUID.randomUUID()),
                        )
                    )
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "user removed successfully"
                    body<ListData> {
                        description = "the updated list"
                    }
                }
                HttpStatusCode.NotFound to {
                    description = "list not found"
                }
            }
        }) {
            val userId = userIdFromSessionOrThrow()
            val listId = it.parent.list_id

            val userToRemoveId = call.receive<ListData.ListPermissionRemoveRequestData>().user_id

            val updatedList = listDao.removePermissionFromUser(userId, listId, userToRemoveId)
                ?: return@delete call.respond(HttpStatusCode.NotFound)

            emitWebsocketEvent(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.LIST_UPDATED,
                content = WebsocketEventContent.ListCreateOrUpdateEventContent(updatedList)
            )

            call.respond(updatedList)
        }
    }

    post<ListsRoute.AcceptInvitation>({
        tags = listOf("list")
        operationId = "accept-list-invitation"
        summary = "accepts a list invitation via a token"
        description = "a user can accept a list invitation via a token that is sent via email when he is invited"
        protected = false
        request {
            queryParameter<String>("token") {
                description = "invitation token"
                required = true
                allowEmptyValue = false
                allowReserved = false
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "list invitation accepted"
                body<ListData> {
                    description = "the updated list"
                }
            }
            HttpStatusCode.NotFound to {
                description = "something went wrong"
            }
            HttpStatusCode.MethodNotAllowed to {
                description = "you need to have an account to accept this invitation"
            }
        }
    }) { request ->
        val listInvitationData = listInvitationDao.get(request.token)
            ?: return@post call.respond(HttpStatusCode.NotFound)

        val invitedUser = userDao.getFromEmail(listInvitationData.email)
            ?: return@post call.respond(HttpStatusCode.MethodNotAllowed, "you need an account to accept the invitation")

        val list = listDao.getByIdOnly(listInvitationData.listId)
            ?: return@post call.respond(HttpStatusCode.NotFound)

        val addAsViewer = !listInvitationData.editor && list.viewers.none { user -> user == invitedUser.id }
        val addAsEditor = listInvitationData.editor && list.editors.none { user -> user == invitedUser.id }

        if (addAsViewer || addAsEditor) {
            val updatedList = if (listInvitationData.editor) {
                listDao.addPermissionToUser(list.user_id, list.id, invitedUser.id, true)
            } else {
                listDao.addPermissionToUser(list.user_id, list.id, invitedUser.id, false)
            }

            if (updatedList == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(updatedList)

                emitWebsocketEvent(
                    websocketEventManager = websocketEventManager,
                    type = WebsocketEventType.LIST_UPDATED,
                    content = WebsocketEventContent.ListCreateOrUpdateEventContent(updatedList)
                )
            }
        } else {
            // user already has permissions
            call.respond(list)
        }
    }
}