package app.index_it.api.routing.websocket

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

fun Route.websocketRoutes() {
    authenticate("auth-user-session") {
        webSocket("/ws") {
            // Save connected device either in Redis or in ram
            // If in Redis then when an event needs to be transmitted, you should search for the receiver first based on who saved the user in Redis
            // (not recommended imo) Otherwise propagate it to all nodes and each node should check if the user is connected to him
            // Set up a listener for RabbitMq
            // Listen to the queue
            // When receiving check if the client should receive the message
            // Send the frame --> sendSerialized(data) if it's not already json, otherwise send()
        }
    }
}
