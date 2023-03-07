package app.index_it.api.routing.websocket

import app.index_it.core.logic.websocket.WebsocketConnectionsManager
import app.index_it.models.auth.UserSessionDto
import app.index_it.models.websocket.WebsocketConnection
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import kotlinx.coroutines.channels.ClosedSendChannelException

fun Route.websocketRoutes() {
    authenticate("auth-user-session") {
        webSocket("/ws") {
            val session = call.principal<UserSessionDto>()

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
