package app.index.api.core.logic.websocket

import app.index.api.core.logic.ObjectMapper
import app.index.shared.core.typedId.impl.IxId
import app.index.api.core.logic.websocket.connection.WebsocketConnectionsManager
import app.index.api.core.logic.websocket.event.WebsocketEventContent
import app.index.api.core.logic.websocket.event.WebsocketEventData
import app.index.api.core.logic.websocket.event.WebsocketEventType
import app.index.api.core.logic.websocket.queue.WebsocketEventsQueueManager
import app.index.shared.core.data.models.auth.UserAuthSessionData
import app.index.shared.core.data.models.user.UserData
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.serialization.*
import io.ktor.server.websocket.*
import org.koin.core.annotation.Single

private val log = KotlinLogging.logger {}

@Single(createdAtStart = true)
class WebsocketEventManager(
    private val websocketEventsQueueManager: WebsocketEventsQueueManager,
    private val websocketConnectionsManager: WebsocketConnectionsManager,
    private val objectMapper: ObjectMapper
) {
    init {
        websocketEventsQueueManager.startListeningAndConsumingEvents { body ->
            if (body == null) {
                log.error { "Missing rabbitmq message body in websocket queue" }
            } else {
                val websocketEventData: WebsocketEventData = objectMapper.decodeFromByteArray(body)

                log.debug { "Consuming rabbitMq websocket event: $websocketEventData" }

                consume(websocketEventData)
            }
        }
    }

    private suspend fun consume(websocketEventData: WebsocketEventData) {
        if (websocketEventData.type == WebsocketEventType.USER_AUTH_SESSIONS_INVALIDATED) {
            if (websocketEventData.fromUserId == null) {
                throw IllegalArgumentException("Emitted a USER_AUTH_SESSIONS_INVALIDATED websocket event but the event payload FROM_USER_ID property is null")
            } else {
                websocketConnectionsManager.closeAllSessionsOfUser(websocketEventData.fromUserId)
            }
            return
        }

        val targetWebsocketConnections  = websocketEventData.targetUsers.map {
            websocketConnectionsManager.getConnectionsOfUser(it)
        }.flatten().toMutableSet()

        if (!websocketEventData.inclusive) {
            targetWebsocketConnections.removeIf { it.sessionId == websocketEventData.fromSessionId }
        }

        targetWebsocketConnections.forEach {
            try {
                it.connection.sendSerialized(websocketEventData.sanitize())
                log.debug { "Sent websocket event to websocket connection: $it" }
            } catch (_: IllegalStateException) {
                // Websocket connection is closed already
                websocketConnectionsManager.removeConnection(it)
            } catch (e: WebsocketConverterNotFoundException) {
                log.error(e) { "Could not find websocket converter for serialization" }
            }
        }
    }

    /**
     * Emits a websocket event to all [users]
     *
     * @param fromSessionId null if websocket event was not triggered by a logged in user
     * @param fromUserId null if websocket event was not triggered by a logged in user
     * @param eventType
     * @param eventData
     * @param users the users to emit the event to if they have an active websocket connection
     * @param includeCurrentSession whether to emit the event to the session of the user who triggered the event
     *
     * @throws Exception other exceptions handling the event
     */
    fun emit(
        fromSessionId: IxId<UserAuthSessionData>?,
        fromUserId: IxId<UserData>?,
        eventType: WebsocketEventType,
        eventData: WebsocketEventContent,
        users: Set<IxId<UserData>>,
        includeCurrentSession: Boolean
    ) {
        val websocketEventData = WebsocketEventData(
            fromSessionId = fromSessionId,
            fromUserId = fromUserId,
            targetUsers = users,
            type = eventType,
            inclusive = includeCurrentSession,
            content = eventData
        )

        websocketEventsQueueManager.enqueue(websocketEventData)
    }
}
