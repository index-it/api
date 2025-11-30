package app.index.api.routing.user.routes

import app.index.api.routing.user.LogoutRoute
import app.index.core.logic.websocket.connection.WebsocketConnectionsManager
import app.index.data.daos.auth.UserSessionDao
import app.index.data.models.auth.UserSessionCookie
import io.ktor.http.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject

fun Route.logoutRoutes() {
    val userSessionDao by inject<UserSessionDao>()
    val websocketConnectionsManager by inject<WebsocketConnectionsManager>()

    /**
     * terminates the auth session
     *
     * @tag auth
     * @operationId logout
     * @response 200 session terminated
     * @response 401 user not authenticated
     */
    get<LogoutRoute> {
        val session = call.sessions.get<UserSessionCookie>()!!

        userSessionDao.delete(session.user_id, session.session_id)

        call.sessions.clear<UserSessionCookie>()
        call.respond(HttpStatusCode.OK)

        websocketConnectionsManager.closeConnectionOfSession(session.session_id)
    }
}
