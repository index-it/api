package app.index_it.api.routing.websocket

import app.index_it.api.plugins.AuthenticationMethods
import app.index_it.core.logic.websocket.WebsocketConnectionsManager
import app.index_it.models.auth.UserAuthSessionDto
import app.index_it.models.websocket.WebsocketConnection
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun Route.websocketRoutes() {
    authenticate(AuthenticationMethods.userSessionAuth) {
        webSocket("/ws") {
            val session = call.principal<UserAuthSessionDto>()

            if (session == null) {
                // Should never happen since the route is behind authentication
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Not authenticated via session auth"))
            } else {
                // Save the ws connection
                WebsocketConnectionsManager.handleConnection(
                    WebsocketConnection(session.id, session.userId, this)
                )
            }
        }
    }
}
