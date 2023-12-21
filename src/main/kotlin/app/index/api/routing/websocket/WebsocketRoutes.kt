package app.index.api.routing.websocket

import app.index.api.plugins.AuthenticationMethods
import app.index.data.models.auth.UserAuthSessionDto
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun Route.websocketRoutes() {
    authenticate(AuthenticationMethods.USER_SESSION_AUTH) {
        webSocket("/ws") {
            val session = call.principal<UserAuthSessionDto>()

            if (session == null) {
                // Should never happen since the route is behind authentication
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Not authenticated via session auth"))
            } else {
                // Save the ws connection
                /*
                WebsocketConnectionsManager.handleConnection(
                    WebsocketConnection(session.id, session.userId, this)
                )
                 */
            }
        }
    }
}
