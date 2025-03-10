package app.index.api.plugins

import app.index.config.ApiConfig
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.serialization.IdKotlinXSerializationModule
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.models.auth.UserAuthSessionData
import app.index.data.models.user.UserData
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.json.Json
import java.time.Duration

private val log = KotlinLogging.logger {}

fun PipelineContext<Unit, ApplicationCall>.emitWebsocketEventForCurrentSessionUser(
    websocketEventManager: WebsocketEventManager,
    type: WebsocketEventType,
    content: WebsocketEventContent,
    includeCurrentSession: Boolean = false
) {
    val authSession = this.call.principal<UserAuthSessionData>()
        ?: throw IllegalArgumentException("Session ID missing when trying to emitting websocket event")

    try {
        websocketEventManager.emit(
            fromSessionId = authSession.id,
            fromUserId = authSession.userId,
            eventType = type,
            eventData = content,
            users = listOf(authSession.userId),
            includeCurrentSession = includeCurrentSession
        )
    } catch (e: Exception) {
        log.error(e) { "Error emitting websocket event: type $type, content $content" }
    }
}

fun PipelineContext<Unit, ApplicationCall>.emitWebsocketEventForUsers(
    websocketEventManager: WebsocketEventManager,
    type: WebsocketEventType,
    content: WebsocketEventContent,
    users: List<IxId<UserData>>,
    includeCurrentSession: Boolean = false
) {
    val authSession = this.call.principal<UserAuthSessionData>()

    try {
        websocketEventManager.emit(
            fromSessionId = authSession?.id,
            fromUserId = authSession?.userId,
            eventType = type,
            eventData = content,
            users = users,
            includeCurrentSession = includeCurrentSession
        )
    } catch (e: Exception) {
        log.error(e) { "Error emitting websocket event: type $type, content $content" }
    }
}

fun Application.configureWebsockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = ApiConfig.webSocketMasking

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
