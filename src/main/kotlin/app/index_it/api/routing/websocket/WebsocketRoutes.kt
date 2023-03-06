package app.index_it.api.routing.websocket

import app.index_it.core.logic.WebsocketsManager
import app.index_it.models.auth.UserSessionDto
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun Route.websocketRoutes() {
    authenticate("auth-user-session") {
        webSocket("/ws") {
            // Save connected user session in caffeine
            val session = call.principal<UserSessionDto>()

            if (session == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Not authenticated via session auth"))
            } else {
                WebsocketsManager.addSession(session.userId, session.id)
            }

            // If in Redis then when an event needs to be transmitted, you should search for the receiver first based on who saved the user in Redis
            // (not recommended imo) Otherwise propagate it to all nodes and each node should check if the user is connected to him
            // Set up a listener for RabbitMq
            // Listen to the queue
            // When receiving check if the client should receive the message
            // Send the frame --> sendSerialized(data) if it's not already json, otherwise send()
        }
    }
}
