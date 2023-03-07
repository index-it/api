package app.index_it.core.logic.websocket

import app.index_it.models.user.UserDto
import app.index_it.models.websocket.WebsocketConnection
import io.ktor.network.sockets.*
import io.ktor.websocket.*
import kotlinx.coroutines.isActive
import org.litote.kmongo.Id
import java.util.*
import kotlin.collections.LinkedHashSet

object WebsocketConnectionsManager {
    private val connections = Collections.synchronizedSet<WebsocketConnection>(LinkedHashSet())

    fun handleConnection(websocketConnection: WebsocketConnection) {
        connections += websocketConnection
    }

    fun getConnectionsOfUserExcludingSession(userId: Id<UserDto>, excludedSessionId: String): List<WebsocketConnection> {
        return connections.filter { it.userId == userId && it.sessionId != excludedSessionId }
    }

    fun removeConnection(websocketConnection: WebsocketConnection) {
        connections.remove(websocketConnection)
    }

    suspend fun closeConnection(sessionId: String) {
        connections.find { it.sessionId == sessionId }?.also {
            it.websocketSession.close(CloseReason(CloseReason.Codes.NORMAL, "Session closed"))
            connections.remove(it)
        }
    }

    suspend fun closeAllSessionsOfUser(userId: Id<UserDto>) {
        connections
            .filter { it.userId == userId }
            .onEach {
                it.websocketSession.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Session closed, not because of logout"))
            }
            .also {
                if (it.isNotEmpty())
                    connections.removeAll(it.toSet())
            }
    }

    /**
     * Closes **ALL** websocket connections
     */
    suspend fun close() {
        connections.onEach { it.websocketSession.close(CloseReason(CloseReason.Codes.SERVICE_RESTART, "Server shutdown")) }
        connections.clear()
    }
}
