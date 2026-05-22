package app.index.api.routing.admin.routes

import app.index.api.core.logic.websocket.WebsocketEventManager
import app.index.api.core.logic.websocket.event.WebsocketEventContent
import app.index.api.core.logic.websocket.event.WebsocketEventType
import app.index.api.data.daos.auth.UserSessionDao
import app.index.api.data.daos.user.UserDao
import app.index.api.plugins.custom.internal
import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.routing.admin.AdminRoute
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
     * Gets a user by its ID
     *
     * Tag: admin
     */
    get<AdminRoute.UsersRoute.UserByIdRoute> {
        val user = userDao.get(it.user_id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(user)
    }.internal()

    /**
     * Deletes a user by its ID
     *
     * Tag: admin
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
    }.internal()
}
