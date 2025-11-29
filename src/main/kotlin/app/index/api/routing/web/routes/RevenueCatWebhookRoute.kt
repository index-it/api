package app.index.api.routing.web.routes

import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.routing.web.WebhookRoute
import app.index.config.RevenueCatConfig
import app.index.core.logic.pro.ProManager
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.models.pro.RevenueCatWebhookRequestData
import app.index.data.models.pro.RevenueCatWebhookRequestWrapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

private val log = KotlinLogging.logger {  }

fun Route.revenueCatWebhookRoute() {
    val proManager by inject<ProManager>()
    val websocketEventManager by inject<WebsocketEventManager>()

    /**
     * receives webhooks for revenuecat events
     *
     * @tag web
     * @operationId revenuecat-webhook
     * @requestBody application/json the webhook data
     * @response 200 handled
     */
    post<WebhookRoute.RevenueCatWebhookRoute> {
        val webhookData = try {
            call.receive<RevenueCatWebhookRequestWrapper>().event
        } catch (e: ContentTransformationException) {
            log.error { e }
            return@post call.respond(HttpStatusCode.BadRequest)
        }

        if (webhookData.environment == RevenueCatWebhookRequestData.RevenueCatEnvironment.SANDBOX && !RevenueCatConfig.sandbox) {
            return@post call.respond(HttpStatusCode.OK)
        }

        if (webhookData.environment == RevenueCatWebhookRequestData.RevenueCatEnvironment.PRODUCTION && RevenueCatConfig.sandbox) {
            return@post call.respond(HttpStatusCode.OK)
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