package app.index.api.routing.websocket

import app.index.api.core.logic.websocket.connection.WebsocketConnection
import app.index.api.core.logic.websocket.connection.WebsocketConnectionsManager
import app.index.api.data.models.auth.UserAuthSessionData
import app.index.api.plugins.AuthenticationMethods
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.koin.ktor.ext.inject

private val log = KotlinLogging.logger {  }

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
                val wsConnection = WebsocketConnection(
                    sessionId = session.id,
                    userId = session.userId,
                    connection = this
                )

                websocketConnectionsManager.handle(wsConnection)

                // Keep connection open until someone closes it
                try {
                    for (frame in incoming) {
                        // Just need to keep this open
                    }
                } catch (e: Exception) {
                    log.warn { "Websocket exception $e" }
                } finally {
                    websocketConnectionsManager.removeConnection(wsConnection)
                }
            }
        }
    }
}
