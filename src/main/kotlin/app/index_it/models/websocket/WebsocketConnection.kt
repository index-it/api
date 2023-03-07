package app.index_it.models.websocket

import app.index_it.models.user.UserDto
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.litote.kmongo.Id

data class WebsocketConnection(
    val sessionId: String,
    val userId: Id<UserDto>,
    val websocketSession: WebSocketServerSession
)
