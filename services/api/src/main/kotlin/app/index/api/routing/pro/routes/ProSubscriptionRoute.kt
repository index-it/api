package app.index.api.routing.pro.routes

import app.index.api.core.logic.pro.ProManager
import app.index.api.core.logic.websocket.WebsocketEventManager
import app.index.api.core.logic.websocket.event.WebsocketEventContent
import app.index.api.core.logic.websocket.event.WebsocketEventType
import app.index.api.plugins.emitWebsocketEventForCurrentSessionUser
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.pro.ProRoute
import io.ktor.http.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

@Suppress("UNUSED")
fun Route.proSubscriptionRoute() {
    val proManager by inject<ProManager>()
    val websocketEventManager by inject<WebsocketEventManager>()

    /**
     * Restores a user subscription if it is somehow missing in the Index system.
     *
     * Tag: pro
     *
     * Security: session
     */
    get<ProRoute.SubscriptionRoute.RestoreRoute> {
        val userId = userIdFromSessionOrThrow()

        val updatedUserData = proManager.refreshProStatus(
            userIds = listOf(userId.toString())
        ) ?: return@get call.respond(HttpStatusCode.NoContent)

        call.respond(updatedUserData.getResponseDto())

        emitWebsocketEventForCurrentSessionUser(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.USER_UPDATED,
            content = WebsocketEventContent.UserUpdateEventContent(updatedUserData.getResponseDto())
        )
    }
}