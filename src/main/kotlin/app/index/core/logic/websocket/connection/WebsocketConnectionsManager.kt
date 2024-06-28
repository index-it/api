package app.index.core.logic.websocket.connection

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.auth.UserAuthSessionData
import app.index.data.models.user.UserData
import app.index.di.IClosableComponent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.websocket.*
import org.koin.core.annotation.Single
import java.util.*

private val log = KotlinLogging.logger { }

@Single(createdAtStart = true)
class WebsocketConnectionsManager : IClosableComponent {
    private val connections = Collections.synchronizedSet<WebsocketConnection>(LinkedHashSet())

    fun handle(websocketConnection: WebsocketConnection) {
        // Max one connection per session
        connections.removeAll { it.sessionId == websocketConnection.sessionId }

        connections += websocketConnection
        log.debug { "Handling new websocket connection: $websocketConnection" }
    }

    fun getConnectionsOfUser(userId: IxId<UserData>): List<WebsocketConnection> {
        return connections.filter { it.userId == userId }
    }

    fun removeConnection(websocketConnection: WebsocketConnection) {
        connections.remove(websocketConnection)
        log.debug { "Not handling the following websocket connection anymore: $websocketConnection" }
    }

    suspend fun closeConnectionOfSession(id: IxId<UserAuthSessionData>) {
        connections.find { it.sessionId == id }
            ?.also {
                try {
                    it.connection.close(CloseReason(CloseReason.Codes.NORMAL, "Session closed"))
                } catch (_: Exception) {}
                connections.remove(it)
                log.debug { "Closed websocket connection: $it" }
            }
    }

    suspend fun closeAllSessionsOfUser(userId: IxId<UserData>) {
        connections
            .filter { it.userId == userId }
            .onEach {
                try {
                    it.connection.close(
                        CloseReason(
                            CloseReason.Codes.CANNOT_ACCEPT,
                            "Session closed, not because of logout"
                        )
                    )
                } catch (_: Exception) {}
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
    override suspend fun close() {
        connections.onEach {
            try {
                it.connection.close(
                    CloseReason(
                        CloseReason.Codes.SERVICE_RESTART,
                        "Server shutdown"
                    )
                )
            } catch (_: Exception) {}
        }
        connections.clear()
        log.debug { "Closed all websocket sessions" }
    }
}