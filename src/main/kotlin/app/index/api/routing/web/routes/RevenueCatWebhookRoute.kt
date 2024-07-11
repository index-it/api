package app.index.api.routing.web.routes

import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.routing.web.WebhookRoute
import app.index.core.logic.pro.ProManager
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.models.pro.RevenueCatWebhookRequestData
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.revenueCatWebhookRoute() {
    val proManager by inject<ProManager>()
    val websocketEventManager by inject<WebsocketEventManager>()

    post<WebhookRoute.RevenueCatWebhookRoute>({
        tags = listOf("web")
        operationId = "revenuecat-webhook"
        summary = "receives webhooks for revenuecat events"
        request {
            body<RevenueCatWebhookRequestData> {
                description = "the webhook data"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "handled"
            }
        }
    }) {
        val webhookData = try {
            call.receive<RevenueCatWebhookRequestData>()
        } catch (e: ContentTransformationException) {
            return@post call.respond(HttpStatusCode.BadRequest)
        }

        val updatedUserData = proManager.refreshProStatus(
            userIds = listOf(webhookData.original_app_user_id) + webhookData.aliases
        )

        call.respond(HttpStatusCode.OK)

        if (updatedUserData != null) {
            emitWebsocketEventForUsers(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.USER_UPDATED,
                content = WebsocketEventContent.UserUpdateEventContent(updatedUserData.getResponseDto()),
                users = listOf(updatedUserData.id)
            )
        }
    }
}