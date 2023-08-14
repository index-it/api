package app.index_it.core.logic.websocket

import app.index_it.api.plugins.userIdFromSession
import app.index_it.core.logic.ObjectMapper
import app.index_it.models.auth.UserAuthSessionDto
import app.index_it.models.websocket.RabbitMqWebsocketEventDto
import app.index_it.models.websocket.RabbitMqWebsocketEventType
import app.index_it.models.websocket.WebsocketFrameDataDto
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*
import mu.KotlinLogging

private val log = KotlinLogging.logger{}

object WebsocketEventManager {
    fun rabbitMqWebsocketEventDtoFromRestCall(context: PipelineContext<Unit, ApplicationCall>, eventType: RabbitMqWebsocketEventType, eventData: Any?): RabbitMqWebsocketEventDto {
        val sessionId = context.call.principal<UserAuthSessionDto>()?.id
            ?: throw IllegalArgumentException("Session ID missing when trying to construct websocket event")

        val userId = context.userIdFromSession()
            ?: throw IllegalArgumentException("User ID missing when trying to construct websocket event")

        return RabbitMqWebsocketEventDto(
            sessionId,
            userId,
            eventType,
            "missing"
            // ObjectMapper.encode(eventData) // TODO: Can't encode any!
        )
    }

    suspend fun emit(rabbitMqWebsocketEventDto: RabbitMqWebsocketEventDto) {
        try {
            WebsocketsQueueManager.enqueue(rabbitMqWebsocketEventDto)
            consume(rabbitMqWebsocketEventDto)
        } catch (e: Exception) {
            /*
              I don't want the API server to handle this and perhaps respond with a 500 status code
              Since websockets aren't related to the success of the http call
             */
            log.error("Unhandled exception in the websocket event manager", e)
        }
    }

    suspend fun consume(rabbitMqWebsocketEventDto: RabbitMqWebsocketEventDto) {
        log.debug { "Consuming rabbitMq websocket event: $rabbitMqWebsocketEventDto" }
        if (rabbitMqWebsocketEventDto.eventType.realTimeDataKind)
            consumeRealTimeDataEvent(rabbitMqWebsocketEventDto)
        else when(rabbitMqWebsocketEventDto.eventType) {
            RabbitMqWebsocketEventType.CLOSE_ALL_CLIENT_CONNECTIONS -> consumeCloseAllUserWebsocketConnectionsEvent(rabbitMqWebsocketEventDto)
            else -> {}
        }
    }

    private suspend fun consumeRealTimeDataEvent(rabbitMqWebsocketEventDto: RabbitMqWebsocketEventDto) {
        log.debug { "Consuming real time data event: $rabbitMqWebsocketEventDto" }

        val userLocalConnections = WebsocketConnectionsManager.getConnectionsOfUserExcludingSession(
            userId = rabbitMqWebsocketEventDto.fromUserId,
            excludedSessionId = rabbitMqWebsocketEventDto.fromSessionId
        )

        if (userLocalConnections.isNotEmpty()) {
            userLocalConnections.forEach {
                try {
                    it.websocketSession.sendSerialized(WebsocketFrameDataDto.fromWebsocketEvent(rabbitMqWebsocketEventDto))
                    log.debug { "Sent websocket event to websocket session: $it" }
                } catch (e: IllegalStateException) {
                    WebsocketConnectionsManager.removeConnection(it)
                } catch (e: WebsocketConverterNotFoundException) {
                    log.error("Could not find websocket converter for serialization", e)
                }
            }
        }
    }

    private suspend fun consumeCloseAllUserWebsocketConnectionsEvent(rabbitMqWebsocketEventDto: RabbitMqWebsocketEventDto) {
        WebsocketConnectionsManager.closeAllSessionsOfUser(rabbitMqWebsocketEventDto.fromUserId)
    }
}
