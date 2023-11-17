package app.index_it.api.plugins

import app.index_it.core.logic.typedId.serialization.IdKotlinXSerializationModule
import app.index_it.core.logic.websocket.WebsocketEventManager
import app.index_it.data.models.websocket.RabbitMqWebsocketEventType
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.json.Json
import java.time.Duration

private val logger = KotlinLogging.logger {}

suspend fun PipelineContext<Unit, ApplicationCall>.emitRabbitMqWebsocketEvent(eventType: RabbitMqWebsocketEventType, eventData: Any?) {
    try {
        val websocketEventDto = WebsocketEventManager.rabbitMqWebsocketEventDtoFromRestCall(this, eventType, eventData)
        WebsocketEventManager.emit(websocketEventDto)
    } catch (e: Exception) {
        logger.error(e) { "Error emitting websocket event (event type $eventType, event data $eventData)" }
    }
}

fun Application.configureWebsockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = true

        contentConverter = KotlinxWebsocketSerializationConverter(
            Json {
                serializersModule = IdKotlinXSerializationModule
            }
        )

        // If frames get too big compression should get implemented!
        /*
        extensions {
            install(WebSocketDeflateExtension) {
                /**
                 * Compression level to use for [java.util.zip.Deflater].
                 */
                compressionLevel = Deflater.DEFAULT_COMPRESSION

                /**
                 * Prevent compressing small outgoing frames.
                 */
                compressIfBiggerThan(bytes = 4 * 1024)
            }
        }
         */
    }
}
