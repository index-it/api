package app.index.api.routing.pro.routes

import app.index.api.plugins.emitWebsocketEventForCurrentSessionUser
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.pro.ProRoute
import app.index.core.logic.pro.ProManager
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.models.user.UserData
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

@Suppress("UNUSED")
fun Route.proSubscriptionRoute() {
    val proManager by inject<ProManager>()
    val websocketEventManager by inject<WebsocketEventManager>()

    get<ProRoute.SubscriptionRoute.RestoreRoute>({
        tags = listOf("pro")
        operationId = "pro-restore-subscription"
        summary = "restores a user subscription if it is somehow missing in the Index system"
        response {
            HttpStatusCode.OK to {
                description = "subscription restored"
                body<UserData> {
                    description = "the new user data with the updated subscription status"
                }
            }
            HttpStatusCode.NoContent to {
                description = "user status is already up to date"
            }
            HttpStatusCode.Unauthorized to {
                description = "user not authenticated"
            }
        }
    }) {
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