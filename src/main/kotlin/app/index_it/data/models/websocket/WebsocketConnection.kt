package app.index_it.data.models.websocket

import app.index_it.data.models.auth.UserAuthSessionDto
import app.index_it.data.models.user.UserDto
import io.ktor.server.websocket.*
import org.litote.kmongo.Id

data class WebsocketConnection(
    val sessionId: Id<UserAuthSessionDto>,
    val userId: Id<UserDto>,
    val websocketSession: WebSocketServerSession
)
