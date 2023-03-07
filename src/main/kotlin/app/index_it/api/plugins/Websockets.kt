package app.index_it.api.plugins

import app.index_it.core.logic.websocket.WebsocketEventManager
import app.index_it.models.websocket.RabbitMqWebsocketEventType
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule
import java.time.Duration

private val logger = KotlinLogging.logger {}

suspend fun PipelineContext<Unit, ApplicationCall>.emitRabbitMqWebsocketEvent(eventType: RabbitMqWebsocketEventType, eventData: Any?) {
    try {
        val websocketEventDto = WebsocketEventManager.rabbitMqWebsocketEventDtoFromRestCall(this, eventType, eventData)
        WebsocketEventManager.emit(websocketEventDto)
    } catch (e: Exception) {
        logger.error("Error emitting websocket event (event type $eventType, event data $eventData)", e)
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

        // If frames get too bit compression should get implemented!
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
