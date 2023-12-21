package app.index.data.models.websocket

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.auth.UserAuthSessionData
import app.index.data.models.user.UserData
import io.ktor.server.websocket.*

data class WebsocketConnection(
    val sessionId: IxId<UserAuthSessionData>,
    val userId: IxId<UserData>,
    val websocketSession: WebSocketServerSession,
)
