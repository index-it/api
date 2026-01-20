 package app.index.api.routing.list.routes

import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.list.ListDao
import app.index.data.daos.list.ListUserInviteDao
import app.index.data.daos.user.UserDao
import io.ktor.http.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

 fun Route.listAcceptInvitationRoute() {
     val listDao by inject<ListDao>()
     val userDao by inject<UserDao>()
     val listUserInviteDao by inject<ListUserInviteDao>()
     val websocketEventManager by inject<WebsocketEventManager>()

    /**
     * accepts a list invitation via a token
     *
     * a user can accept a list invitation via a token that is sent via email when he is invited
     *
     * @tag lists-access
     * @operationId accept-list-invitation
     * @query token invitation token
     * @response 200 list invitation accepted
     * @response 404 something went wrong
     * @response 405 you need to have an account to accept this invitation
     */
    get<ListsRoute.AcceptInvitation> { request ->
        val listInvitationData = listUserInviteDao.get(request.token)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        val invitedUser = userDao.getFromEmail(listInvitationData.email)
            ?: return@get call.respond(HttpStatusCode.MethodNotAllowed, "you need an account to accept the invitation")

        val list = listDao.get(listInvitationData.listId)
            ?: return@get call.respond(HttpStatusCode.NotFound)

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
}