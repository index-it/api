package app.index.data.models.websocket

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.auth.UserAuthSessionDto
import app.index.data.models.user.UserDto
import io.ktor.server.websocket.*

data class WebsocketConnection(
    val sessionId: IxId<UserAuthSessionDto>,
    val userId: IxId<UserDto>,
    val websocketSession: WebSocketServerSession,
)
