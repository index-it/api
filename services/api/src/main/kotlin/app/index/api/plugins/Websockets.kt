package app.index.api.plugins

import app.index.shared.core.config.ApiConfig
import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.typedId.serialization.IdKotlinXSerializationModule
import app.index.api.core.logic.websocket.WebsocketEventManager
import app.index.api.core.logic.websocket.event.WebsocketEventContent
import app.index.api.core.logic.websocket.event.WebsocketEventType
import app.index.shared.core.data.models.auth.UserAuthSessionData
import app.index.shared.core.data.models.user.UserData
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

private val log = KotlinLogging.logger {}

fun RoutingContext.emitWebsocketEventForCurrentSessionUser(
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
            users = setOf(authSession.userId),
            includeCurrentSession = includeCurrentSession
        )
    } catch (e: Exception) {
        log.error(e) { "Error emitting websocket event: type $type, content $content" }
    }
}

fun RoutingContext.emitWebsocketEventForUsers(
    websocketEventManager: WebsocketEventManager,
    type: WebsocketEventType,
    content: WebsocketEventContent,
    users: Set<IxId<UserData>>,
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
        pingPeriod = 15.seconds
        timeout = 15.seconds
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
