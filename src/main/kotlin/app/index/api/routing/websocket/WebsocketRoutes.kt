package app.index.api.routing.websocket

import app.index.api.plugins.AuthenticationMethods
import app.index.core.logic.websocket.connection.WebsocketConnection
import app.index.core.logic.websocket.connection.WebsocketConnectionsManager
import app.index.data.models.auth.UserAuthSessionData
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.koin.ktor.ext.inject

fun Route.websocketRoutes() {
    val websocketConnectionsManager by inject<WebsocketConnectionsManager>()

    authenticate(AuthenticationMethods.USER_SESSION_AUTH) {
        webSocket("/ws") {
            val session = call.principal<UserAuthSessionData>()

            if (session == null) {
                // Should never happen since the route is behind authentication
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Not authenticated via session auth"))
            } else {
                // Save the ws connection
                websocketConnectionsManager.handle(
                    WebsocketConnection(
                        sessionId = session.id,
                        userId = session.userId,
                        connection = this
                    )
                )
            }
        }
    }
}
