package app.index.api.routing.list.routes

import app.index.api.core.logic.usecases.ListAuthorizationUseCase
import app.index.api.core.logic.usecases.ListInvitationUseCase
import app.index.api.core.logic.websocket.WebsocketEventManager
import app.index.api.core.logic.websocket.event.WebsocketEventContent
import app.index.api.core.logic.websocket.event.WebsocketEventType
import app.index.api.data.daos.list.ListDao
import app.index.api.data.daos.user.UserDao
import app.index.api.data.models.lists.ListAuthorizationLevel
import app.index.api.data.models.lists.ListData
import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.listAccessUsersRoute() {
    val listDao by inject<ListDao>()
    val userDao by inject<UserDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

    /**
     * Gives informations about the users that have access to the list.
     *
     * Tag: lists-access
     *
     * Security: session
     */
    get<ListsRoute.ListRoute.AccessRoute.UsersRoute> {
        val listId = it.parent.parent.list_id

        ListAuthorizationUseCase.getListIfAuthorized(
            listId = listId,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.OWNER
        ) ?: return@get call.respond(HttpStatusCode.NotFound)

        val userAccessInfo = listDao.getListUserAccessInfo(listId)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(userAccessInfo)
    }

    /**
     * Invites a user to have access to a list or changes their permissions if already added.
     *
     * Tag: lists-access
     *
     * Security: session
     */
    post<ListsRoute.ListRoute.AccessRoute.UsersRoute> {
        val userId = userIdFromSessionOrThrow()
        val listId = it.parent.parent.list_id

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
            !permissionInfo.editor && (invitedUser == null || list.viewers.none { user -> user == invitedUser.id })
        val addAsEditor =
            permissionInfo.editor && (invitedUser == null || list.editors.none { user -> user == invitedUser.id })
        val hasAlreadyAcceptedInvitation =
            invitedUser != null && (list.editors.any { user -> user == invitedUser.id } || list.editors.any { user -> user == invitedUser.id })

        if (!addAsViewer && !addAsEditor) {
            // user is already added, no need to perform any action
            call.respond(list)
        } else if (hasAlreadyAcceptedInvitation) {
            // user has already accepted the invitation, just update the permission
            val updatedList = if (permissionInfo.editor) {
                listDao.addPermissionToUser(listId, invitedUser.id, true)
            } else {
                listDao.addPermissionToUser(listId, invitedUser.id, false)
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
            val sent = ListInvitationUseCase.sendInvitation(
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

    /**
     * Removes access from a user to the list.
     *
     * Tag: lists-access
     *
     * Security: session
     */
    delete<ListsRoute.ListRoute.AccessRoute.UsersRoute> {
        val listId = it.parent.parent.list_id

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

    /**
     * Removes the user from the viewers or editors of the list.
     *
     * Tag: lists-access
     *
     * Security: session
     */
    get<ListsRoute.ListRoute.AccessRoute.LeaveRoute> {
        val userId = userIdFromSessionOrThrow()
        val listId = it.parent.parent.list_id
        val list = listDao.get(listId)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        if (list.user_id == userId) {
            return@get call.respond(
                HttpStatusCode.MethodNotAllowed,
                "you cannot leave the list as you are the owner, try deleting it instead"
            )
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