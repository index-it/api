package app.index.api.routing.list.routes

import app.index.api.plugins.emitWebsocketEvent
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.usecases.ListInvitationUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.list.ListDao
import app.index.data.daos.user.UserDao
import app.index.data.models.lists.ListData
import io.github.smiley4.ktorswaggerui.dsl.resources.delete
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.listRoute() {
    val listDao by inject<ListDao>()
    val userDao by inject<UserDao>()
    val listInvitationUseCase by inject<ListInvitationUseCase>()
    val websocketEventManager by inject<WebsocketEventManager>()

    get<ListsRoute.ListRoute>({
        tags = listOf("lists")
        operationId = "get-list"
        summary = "gets a single list"
        request {
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the list"
                body<ListData>()
            }
            HttpStatusCode.NotFound to {
                description = "list not found"
            }
        }
    }) {
        val list = listDao.get(userIdFromSessionOrThrow(), it.list_id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(list)
    }

    put<ListsRoute.ListRoute>({
        tags = listOf("lists")
        operationId = "update-list"
        summary = "updates a list"
        request {
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
            body<ListData.ListUpdateRequestData> {
                description = "the new values for the list"
                required = true
                example("sample-update", ListData.ListUpdateRequestData("locations", "📍", "#343322"))
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "list updated"
                body<ListData> {
                    description = "the updated list"
                }
            }
            HttpStatusCode.NotFound to {
                description = "list not found"
            }
        }
    }) {
        val updatedList = call.receive<ListData.ListUpdateRequestData>()

        val newList = listDao.update(userIdFromSessionOrThrow(), it.list_id, updatedList)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(newList)

        emitWebsocketEvent(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.LIST_UPDATED,
            content = WebsocketEventContent.ListCreateOrUpdateEventContent(newList)
        )
    }

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
                description = "the user to invite and whether to grant him edit permission, otherwise if editor is set to false he will be invited as a viewer"
                required = true
                example("sample-user-permission-update", ListData.ListPermissionAddRequestData(
                    email = "j@index-it.app",
                    editor = false
                ))
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "user invited or permissions updated"
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
        val addAsViewer = invitedUser == null || (!permissionInfo.editor && list.viewers.none { user -> user == invitedUser.id })
        val addAsEditor = invitedUser == null || (permissionInfo.editor && list.editors.none { user -> user == invitedUser.id })
        val hasAlreadyAcceptedInvitation = invitedUser != null && (list.editors.any { user -> user == invitedUser.id } || list.editors.any { user -> user == invitedUser.id })

        if (!addAsViewer && !addAsEditor) {
            // user is already added, no need to perform any action
            call.respond(HttpStatusCode.OK)
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

    delete<ListsRoute.ListRoute>({
        tags = listOf("lists")
        operationId = "delete-list"
        summary = "deletes a list"
        description = "this deletes the list and **all** of its content, meaning categories, items and item contents of the list will be deleted"
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
        }
    }) {
        val deleted = listDao.delete(userIdFromSessionOrThrow(), it.list_id)

        call.respond(HttpStatusCode.OK)

        if (deleted) {
            emitWebsocketEvent(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.LIST_DELETED,
                content = WebsocketEventContent.ListDeleteEventContent(it.list_id)
            )
        }
    }
}
