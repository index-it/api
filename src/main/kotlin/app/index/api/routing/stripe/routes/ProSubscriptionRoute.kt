package app.index.api.routing.stripe.routes

import app.index.api.plugins.emitWebsocketEventForCurrentSessionUser
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.stripe.ProRoute
import app.index.core.exceptions.AuthenticationException
import app.index.core.logic.pro.ProManager
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.user.UserDao
import app.index.data.models.pro.ProSubscriptionCancellationRequestData
import app.index.data.models.user.UserData
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.proSubscriptionRoute() {
    val userDao by inject<UserDao>()
    val proManager by inject<ProManager>()
    val websocketEventManager by inject<WebsocketEventManager>()

    get<ProRoute.SubscriptionRoute.CreateRoute>({
        tags = listOf("stripe")
        operationId = "stripe-create-subscription"
        summary = "creates a payment intent that allows the user to purchase a subscription to the pro version"
        request {
            queryParameter<String>("price_id") {
                required = true
                description = "the id of the price the user wants to subscribe to"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "payment intent created"
                body<String> {
                    description = "the client_secret from the subscription’s first payment intent"
                }
            }
            HttpStatusCode.Unauthorized to {
                description = "user not authenticated"
            }
            HttpStatusCode.BadRequest to {
                description = "invalid plan_id"
            }
            HttpStatusCode.MethodNotAllowed to {
                description = "user already has an active subscription"
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()
        val user = userDao.get(userId)
            ?: throw AuthenticationException()
        val priceId = it.price_id

        if (user.stripe_customer_id != null && proManager.hasActiveSubscription(user.stripe_customer_id)) {
            return@get call.respond(HttpStatusCode.MethodNotAllowed)
        }

        val paymentIntentClientSecret = proManager.createSubscription(
            customerId = user.stripe_customer_id,
            userId = user.id,
            email =  user.email,
            priceId = priceId,
            promotionCode = it.promotion_code
        )

        if (paymentIntentClientSecret == null) {
            return@get call.respond(HttpStatusCode.Created)
        }

        call.respond(paymentIntentClientSecret)
    }

    get<ProRoute.SubscriptionRoute.RestoreRoute>({
        tags = listOf("stripe")
        operationId = "stripe-restore-subscription"
        summary = "restores a user subscription if it is somehow missing in the Index system"
        response {
            HttpStatusCode.OK to {
                description = "subscription restored"
                body<UserData> {
                    description = "the updated user data with the restored subscription information"
                }
            }
            HttpStatusCode.Unauthorized to {
                description = "user not authenticated"
            }
            HttpStatusCode.NotFound to {
                description = "user doesn't have any subscription"
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()
        val user = userDao.get(userId)
            ?: throw AuthenticationException()

        if (user.stripe_customer_id == null) {
            return@get call.respond(HttpStatusCode.NotFound)
        }

        val (subscriptionId, priceId) = proManager.getActiveSubscription(user.stripe_customer_id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        val userData = userDao.setStripeSubscriptionData(
            id = user.id,
            subscriptionId = subscriptionId,
            priceId = priceId
        ) ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(userData)

        emitWebsocketEventForCurrentSessionUser(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.USER_UPDATED,
            content = WebsocketEventContent.UserUpdateEventContent(userData.getResponseDto())
        )
    }

    post<ProRoute.SubscriptionRoute.CancelRoute>({
        tags = listOf("stripe")
        operationId = "stripe-cancel-subscription"
        summary = "cancels the user subscription if he has an active one"
        request {
            body<ProSubscriptionCancellationRequestData> {
                description = "optional additional information about why the user canceled the subscription"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "subscription canceled"
            }
            HttpStatusCode.Unauthorized to {
                description = "user not authenticated"
            }
            HttpStatusCode.MethodNotAllowed to {
                description = "user doesn't have an active subscription"
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()
        val user = userDao.get(userId)
            ?: throw AuthenticationException()

        val cancellationInfo = try {
            call.receive<ProSubscriptionCancellationRequestData>()
        } catch (e: ContentTransformationException) {
            ProSubscriptionCancellationRequestData(null, null)
        }

        if (user.stripe_subscription_id == null) {
            return@post call.respond(HttpStatusCode.MethodNotAllowed)
        }

        val canceled = proManager.cancelSubscription(user.stripe_subscription_id, cancellationInfo)

        if (!canceled) {
            return@post call.respond(HttpStatusCode.MethodNotAllowed)
        }

        call.respond(HttpStatusCode.OK)
    }
}