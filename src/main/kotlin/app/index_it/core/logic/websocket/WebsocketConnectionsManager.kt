package app.index_it.core.logic.websocket

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.auth.UserAuthSessionDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.models.websocket.WebsocketConnection
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.websocket.*
import java.util.*

private val log = KotlinLogging.logger {  }
object WebsocketConnectionsManager {
    /*
    private val connections = Collections.synchronizedSet<WebsocketConnection>(LinkedHashSet())

    fun handleConnection(websocketConnection: WebsocketConnection) {
        connections += websocketConnection
        log.debug { "Handling new websocket connection: $websocketConnection" }
    }

    fun getConnectionsOfUserExcludingSession(userId: IxId<UserDto>, excludedSessionId: IxId<UserAuthSessionDto>): List<WebsocketConnection> {
        return connections.filter { it.userId == userId && it.sessionId != excludedSessionId }
    }

    fun removeConnection(websocketConnection: WebsocketConnection) {
        connections.remove(websocketConnection)
        log.debug { "Not handling the following websocket connection anymore: $websocketConnection" }
    }

    suspend fun closeConnection(sessionId: IxId<UserAuthSessionDto>) {
        connections.find { it.sessionId == sessionId }?.also {
            it.websocketSession.close(CloseReason(CloseReason.Codes.NORMAL, "Session closed"))
            log.debug { "Closed websocket session: $it" }
            connections.remove(it)
        }
    }

    suspend fun closeAllSessionsOfUser(userId: IxId<UserDto>) {
        connections
            .filter { it.userId == userId }
            .onEach {
                it.websocketSession.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Session closed, not because of logout"))
            }
            .also {
                if (it.isNotEmpty())
                    connections.removeAll(it.toSet())

                log.debug { "Closed all websocket sessions of user with id $userId" }
            }
    }

    /**
     * Closes **ALL** websocket connections
     */
    suspend fun close() {
        connections.onEach { it.websocketSession.close(CloseReason(CloseReason.Codes.SERVICE_RESTART, "Server shutdown")) }
        connections.clear()
        log.debug { "Closed all websocket sessions" }
    }

     */
}
