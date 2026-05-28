package app.index.api.core.logic.websocket.connection

import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.auth.UserAuthSessionData
import app.index.shared.core.data.models.user.UserData
import io.ktor.server.websocket.*

/**
 * Holds the actual websocket connection of a session authenticated user
 *
 * @param sessionId
 * @param userId
 * @param connection the actual [DefaultWebSocketServerSession]
 */
data class WebsocketConnection(
    val sessionId: IxId<UserAuthSessionData>,
    val userId: IxId<UserData>,
    val connection: DefaultWebSocketServerSession,
)