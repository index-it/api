package app.index.api.routing.list.routes

import app.index.api.plugins.AuthenticationMethods
import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.usecases.ListAuthorizationUseCase
import app.index.core.logic.usecases.ListInvitationUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.list.ListDao
import app.index.data.daos.list.ListInvitationDao
import app.index.data.daos.user.UserDao
import app.index.data.models.lists.ListAuthorizationLevel
import app.index.data.models.lists.ListData
import io.github.smiley4.ktorswaggerui.dsl.resources.delete
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.listAccessRoute() {
    val listDao by inject<ListDao>()
    val userDao by inject<UserDao>()
    val listInvitationUseCase by inject<ListInvitationUseCase>()
    val listInvitationDao by inject<ListInvitationDao>()
    val websocketEventManager by inject<WebsocketEventManager>()


    authenticate(AuthenticationMethods.USER_SESSION_AUTH) {

        post<ListsRoute.ListRoute.AccessRoute>({
            tags = listOf("lists", "lists-access")
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
                HttpStatusCode.Unauthorized to {
                    description = "not authorized to perform this action on the list"
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

            val list = ListAuthorizationUseCase.getListIfAuthorized(
                listId = listId,
                userId = userIdFromSessionOrThrow(),
                authorizationLevel = ListAuthorizationLevel.OWNER
            ) ?: return@post call.respond(HttpStatusCode.NotFound)

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
                    listDao.addPermissionToUser(listId, invitedUser!!.id, true)
                } else {
                    listDao.addPermissionToUser(listId, invitedUser!!.id, false)
                }

                if (updatedList == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(updatedList)

                    emitWebsocketEventForUsers(
                        websocketEventManager = websocketEventManager,
                        type = WebsocketEventType.LIST_UPDATED,
                        content = WebsocketEventContent.ListCreateOrUpdateEventContent(updatedList),
                        users = updatedList.getUsersWithAccess()
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

        delete<ListsRoute.ListRoute.AccessRoute>({
            tags = listOf("lists", "lists-access")
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
                HttpStatusCode.Unauthorized to {
                    description = "not authorized to perform this action on the list"
                }
                HttpStatusCode.NotFound to {
                    description = "list not found"
                }
            }
        }) {
            val listId = it.parent.list_id

            ListAuthorizationUseCase.getListIfAuthorized(
                listId = listId,
                userId = userIdFromSessionOrThrow(),
                authorizationLevel = ListAuthorizationLevel.OWNER
            ) ?: return@delete call.respond(HttpStatusCode.NotFound)

            val userToRemoveId = call.receive<ListData.ListPermissionRemoveRequestData>().user_id

            val updatedList = listDao.removePermissionFromUser(listId, userToRemoveId)
                ?: return@delete call.respond(HttpStatusCode.NotFound)

            emitWebsocketEventForUsers(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.LIST_UPDATED,
                content = WebsocketEventContent.ListCreateOrUpdateEventContent(updatedList),
                users = updatedList.getUsersWithAccess()
            )

            call.respond(updatedList)
        }
    }

    post<ListsRoute.AcceptInvitation>({
        tags = listOf("list", "lists-access")
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

        val list = listDao.get(listInvitationData.listId)
            ?: return@post call.respond(HttpStatusCode.NotFound)

        val addAsViewer = !listInvitationData.editor && list.viewers.none { user -> user == invitedUser.id }
        val addAsEditor = listInvitationData.editor && list.editors.none { user -> user == invitedUser.id }

        if (addAsViewer || addAsEditor) {
            val updatedList = if (listInvitationData.editor) {
                listDao.addPermissionToUser(list.id, invitedUser.id, true)
            } else {
                listDao.addPermissionToUser(list.id, invitedUser.id, false)
            }

            if (updatedList == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(updatedList)

                emitWebsocketEventForUsers(
                    websocketEventManager = websocketEventManager,
                    type = WebsocketEventType.LIST_UPDATED,
                    content = WebsocketEventContent.ListCreateOrUpdateEventContent(updatedList),
                    users = updatedList.getUsersWithAccess()
                )
            }
        } else {
            // user already has permissions
            call.respond(list)
        }
    }

    get<ListsRoute.ListRoute.AccessRoute.LeaveRoute>({
        tags = listOf("lists", "lists-access")
        operationId = "leave-list"
        summary = "removes the user from the viewers or editors of the list"
        description = "the user will be removed from the viewers or editors of the list"
        request {
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "list deleted"
            }
            HttpStatusCode.Unauthorized to {
                description = "not authorized to perform this action on the list"
            }
            HttpStatusCode.MethodNotAllowed to {
                description = "the owner cannot leave the list"
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()
        val listId = it.parent.parent.list_id
        val list = listDao.get(listId)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        if (list.user_id == userId) {
            return@get call.respond(HttpStatusCode.MethodNotAllowed, "you cannot leave the list as you are the owner, try deleting it instead")
        }

        val updatedList = listDao.removePermissionFromUser(listId, userId)
            ?: return@get call.respond(HttpStatusCode.OK)

        call.respond(HttpStatusCode.OK)

        emitWebsocketEventForUsers(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.LIST_UPDATED,
            content = WebsocketEventContent.ListCreateOrUpdateEventContent(updatedList),
            users = updatedList.getUsersWithAccess()
        )
    }
}