package app.index.core.logic.websocket

import app.index.core.logic.ObjectMapper
import app.index.core.logic.websocket.connection.WebsocketConnectionsManager
import app.index.core.logic.websocket.event.WebsocketEventData
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.queue.WebsocketEventsQueueManager
import app.index.data.models.auth.UserAuthSessionData
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

        val websocketConnectionsOfUser = if (websocketEventData.inclusive) {
            websocketConnectionsManager.getConnectionsOfUser(
                userId = websocketEventData.fromUserId
            )
        } else {
            websocketConnectionsManager.getConnectionsOfUserExcludingSession(
                userId = websocketEventData.fromUserId,
                excludedSessionId = websocketEventData.fromSessionId
            )
        }

        websocketConnectionsOfUser.forEach {
            try {
                it.connection.sendSerialized(websocketEventData)
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
     * @throws IllegalArgumentException missing [UserAuthSessionData] principal in [context]
     * @throws Exception other exceptions handling the event
     */
    fun emit(context: PipelineContext<Unit, ApplicationCall>, eventType: WebsocketEventType, eventData: WebsocketEventContent, includeCurrentSession: Boolean) {
        val authSession = context.call.principal<UserAuthSessionData>()
            ?: throw IllegalArgumentException("Session ID missing when trying to emitting websocket event")

        // TODO: Accept more user ids as parameters for shared events such as shared lists
        val websocketEventData = WebsocketEventData(
            fromSessionId = authSession.id,
            fromUserId = authSession.userId,
            type = eventType,
            inclusive = includeCurrentSession,
            content = eventData
        )

        websocketEventsQueueManager.enqueue(websocketEventData)
    }
}
