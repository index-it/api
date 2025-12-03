package app.index.api.routing.admin.routes

import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.routing.admin.AdminRoute
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.auth.UserSessionDao
import app.index.data.daos.user.UserDao
import io.ktor.http.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.adminUsersByIdRoute() {
    val userDao by inject<UserDao>()
    val userSessionDao by inject<UserSessionDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

    /**
     * gets a user by its id
     *
     * @tag admin
     * @operationId get-user-by-id
     * @query user_id the id of the user
     * @response 200 user found
     * @response 404 user with the provided id not found
     */
    get<AdminRoute.UsersRoute.UserByIdRoute> {
        val user = userDao.get(it.user_id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(user)
    }

    /**
     * deletes an user by its id
     *
     * @tag admin
     * @operationId delete-user-by-id
     * @query user_id the id of the user
     * @response 200 user deleted
     */
    delete<AdminRoute.UsersRoute.UserByIdRoute> {
        userDao.delete(it.user_id)
        userSessionDao.deleteAllOfUser(it.user_id)

        call.respond(HttpStatusCode.OK)

        emitWebsocketEventForUsers(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.USER_AUTH_SESSIONS_INVALIDATED,
            content = WebsocketEventContent.EmptyEventContent,
            users = setOf(it.user_id)
        )
    }
}
