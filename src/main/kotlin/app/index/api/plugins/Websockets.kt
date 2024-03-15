package app.index.api.plugins

import app.index.core.logic.typedId.serialization.IdKotlinXSerializationModule
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.core.logic.websocket.event.WebsocketEventContent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.time.Duration

private val log = KotlinLogging.logger {}

fun PipelineContext<Unit, ApplicationCall>.emitWebsocketEvent(
    websocketEventManager: WebsocketEventManager,
    type: WebsocketEventType,
    content: WebsocketEventContent,
    includeCurrentSession: Boolean = false
) {
    try {
        websocketEventManager.emit(this, type, content, includeCurrentSession)
    } catch (e: Exception) {
        log.error(e) { "Error emitting websocket event: type $type, content $content" }
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
                ignoreUnknownKeys = true
                encodeDefaults = true
            }
        )

        // NOTE: If frames get too big compression should be implemented
        // https://ktor.io/docs/websocket-deflate-extension.html
    }
}
