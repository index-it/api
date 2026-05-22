 package app.index.api.routing.list.routes

import app.index.api.core.logic.DatetimeUtils
import app.index.api.core.logic.usecases.ListInvitationUseCase
import app.index.api.core.logic.websocket.WebsocketEventManager
import app.index.api.core.logic.websocket.event.WebsocketEventContent
import app.index.api.core.logic.websocket.event.WebsocketEventType
import app.index.api.data.daos.list.ListDao
import app.index.api.data.daos.list.ListInviteDao
import app.index.api.data.daos.list.ListUserInviteDao
import app.index.api.data.daos.user.UserDao
import app.index.api.plugins.AuthenticationMethods
import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

 fun Route.listAcceptInviteRoute() {
     val listDao by inject<ListDao>()
     val userDao by inject<UserDao>()
     val listInviteDao by inject<ListInviteDao>()
     val listUserInviteDao by inject<ListUserInviteDao>()
     val websocketEventManager by inject<WebsocketEventManager>()

    /**
     * Accepts a user list invitation via a token.
     *
     * Tag: lists-access
     */
    get<ListsRoute.AcceptUserInvite> { request ->
        val listUserInviteData = listUserInviteDao.get(request.token)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        val invitedUser = userDao.getFromEmail(listUserInviteData.email)
            ?: return@get call.respond(HttpStatusCode.MethodNotAllowed, "you need an account to accept the invitation")

        val list = listDao.get(listUserInviteData.listId)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        val (added, updatedList) = ListInvitationUseCase.addPermissionToUser(list, listUserInviteData.editor, invitedUser.id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(updatedList)

        if (added) {
            emitWebsocketEventForUsers(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.LIST_UPDATED,
                content = WebsocketEventContent.ListCreateOrUpdateEventContent(updatedList),
                users = updatedList.getUsersWithAccess()
            )
        }
    }

     authenticate(AuthenticationMethods.USER_SESSION_AUTH) {
          /**
           * Accepts a list invitation via a token.
           *
           * Tag: lists-access
           *
           * Security: session
           */
         get<ListsRoute.AcceptInvite> { request ->
             val userId = userIdFromSessionOrThrow()

             val listInviteData = listInviteDao.get(request.token)
                 ?: return@get call.respond(HttpStatusCode.NotFound)

             if (listInviteData.maxUsages != null && listInviteData.maxUsages < 1) {
                 listInviteDao.delete(listInviteData.id)
                 return@get call.respond(HttpStatusCode.MethodNotAllowed, "This invite has already been used")
             }

             if (listInviteData.expiresAt != null && listInviteData.expiresAt < DatetimeUtils.currentLocalDateTime()) {
                 listInviteDao.delete(listInviteData.id)
                 return@get call.respond(HttpStatusCode.MethodNotAllowed, "This invite has expired")
             }

             val list = listDao.get(listInviteData.listId)
                 ?: return@get call.respond(HttpStatusCode.NotFound)

             val (added, updatedList) = ListInvitationUseCase.addPermissionToUser(list, listInviteData.editor, userId)
                 ?: return@get call.respond(HttpStatusCode.NotFound)

             call.respond(updatedList)

             if (added) {
                 val updatedInvite = listInviteDao.decreaseUsages(listInviteData.id)
                 if (updatedInvite?.maxUsages != null && updatedInvite.maxUsages < 1) {
                     listInviteDao.delete(listInviteData.id)
                 }

                 emitWebsocketEventForUsers(
                     websocketEventManager = websocketEventManager,
                     type = WebsocketEventType.LIST_UPDATED,
                     content = WebsocketEventContent.ListCreateOrUpdateEventContent(updatedList),
                     users = updatedList.getUsersWithAccess()
                 )
             }
         }
     }
}