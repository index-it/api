package app.index_it.data.models.websocket

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.auth.UserAuthSessionDto
import app.index_it.data.models.user.UserDto
import io.ktor.server.websocket.*

data class WebsocketConnection(
    val sessionId: IxId<UserAuthSessionDto>,
    val userId: IxId<UserDto>,
    val websocketSession: WebSocketServerSession
)
