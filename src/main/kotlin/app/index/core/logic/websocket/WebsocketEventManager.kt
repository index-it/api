package app.index.core.logic.websocket

import app.index.core.logic.ObjectMapper
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.websocket.connection.WebsocketConnectionsManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventData
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.core.logic.websocket.queue.WebsocketEventsQueueManager
import app.index.data.models.auth.UserAuthSessionData
import app.index.data.models.user.UserData
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*
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
            websocketConnectionsManager.closeAllSessionsOfUser(websocketEventData.fromUserId)
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
            } catch (e: IllegalStateException) {
                // Websocket connection is closed already
                websocketConnectionsManager.removeConnection(it)
            } catch (e: WebsocketConverterNotFoundException) {
                log.error(e) { "Could not find websocket converter for serialization" }
            }
        }
    }

    /**
     * Emits a websocket event to all [targetUsers]
     *
     * @param fromSessionId
     * @param fromUserId
     * @param eventType
     * @param eventData
     * @param users the users to emit the event to if they have an active websocket connection
     * @param includeCurrentSession whether to emit the event to the session of the user who triggered the event
     *
     * @throws IllegalArgumentException missing [UserAuthSessionData] principal in [context]
     * @throws Exception other exceptions handling the event
     */
    fun emit(
        fromSessionId: IxId<UserAuthSessionData>,
        fromUserId: IxId<UserData>,
        eventType: WebsocketEventType,
        eventData: WebsocketEventContent,
        users: List<IxId<UserData>>,
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
