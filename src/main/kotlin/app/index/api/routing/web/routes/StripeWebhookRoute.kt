package app.index.api.routing.web.routes

import app.index.api.plugins.emitWebsocketEventForCurrentSessionUser
import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.routing.web.WebhookRoute
import app.index.config.StripeConfig
import app.index.core.clients.StripeClient
import app.index.core.exceptions.ConfigurationException
import app.index.core.logic.typedId.toIxId
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.user.UserDao
import com.stripe.exception.SignatureVerificationException
import com.stripe.model.Customer
import com.stripe.model.Subscription
import com.stripe.model.checkout.Session
import com.stripe.net.RequestOptions
import com.stripe.net.Webhook
import com.stripe.param.checkout.SessionRetrieveParams
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.sentry.Sentry
import org.koin.ktor.ext.inject
import kotlin.jvm.optionals.getOrNull

private val log = KotlinLogging.logger {  }

fun Route.stripeWebhookRoute() {
    val userDao by inject<UserDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

    post<WebhookRoute.StripeWebhookRoute>({
        tags = listOf("web")
        operationId = "stripe-webhook"
        summary = "receives webhooks for stripe events"
        response {
            HttpStatusCode.OK to {
                description = "handled"
            }
        }
    }) {
        val payload = call.receive<String>()
        val webhookSecretReceived = call.request.header("Stripe-Signature")

        val event = try {
            Webhook.constructEvent(payload, webhookSecretReceived, StripeConfig.webhookSecret)
        } catch (e: SignatureVerificationException) {
            return@post call.respond(HttpStatusCode.BadRequest)
        }

        // Deserialization failed, probably due to an API version mismatch.
        // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
        // instructions on how to handle this case, or return an error here.
        val stripeObject = event.dataObjectDeserializer.getObject().getOrNull()
            ?: return@post call.respond(HttpStatusCode.InternalServerError)

        when (event.type) {
            "checkout.session.completed" -> {
//                val sessionRetrieveParams = SessionRetrieveParams.builder()
//                    .addExpand("line_items")
//                    .build()
//                val session = Session.retrieve(
//                    /* session = */ stripeObject.rawJsonObject.get("id").asString,
//                    /* params = */ sessionRetrieveParams,
//                    /* options = */ RequestOptions.getDefault()
//                )
//
//                val customerId = session.customer
//                val customer = Customer.retrieve(customerId)
//                val user = customer.metadata[StripeClient.MetadataKeys.INDEX_USER_ID]?.let { userId -> userDao.get(userId.toIxId()) }
//                    ?: run {
//                        val configurationExceptionMessage = "Stripe customer missing required metadata key: ${StripeClient.MetadataKeys.INDEX_USER_ID}"
//
//                        log.error { configurationExceptionMessage }
//                        Sentry.captureException(ConfigurationException(configurationExceptionMessage))
//
//                        userDao.getFromStripeCustomerId(customerId)
//                    }
//
//                // This should logically never occur
//                if (user == null) {
//                    log.error { "Couldn't find index user for stripe customer with id $customerId" }
//                    return@post call.respond(HttpStatusCode.NotFound)
//                }
//
//                val subscriptionId = session.subscription
//                val priceId = session.lineItems.data[0]?.price?.id
//                    ?: run {
//                        log.error { "received stripe checkout.session.complete event with no priceId" }
//                        return@post call.respond(HttpStatusCode.BadRequest)
//                    }
//
//                userDao.setStripeSubscriptionData(
//                    id = user.id,
//                    subscriptionId = subscriptionId,
//                    priceId = priceId
//                )
//
//                call.respond(HttpStatusCode.OK)
                call.respond(HttpStatusCode.OK)
            }
            "customer.subscription.created" -> {
                val sub = stripeObject as Subscription

                // we only update the user info when the subscription is active (paid)
                if (sub.status != "active") {
                    log.info { "received non active stripe subscription webhook" }
                    call.respond(HttpStatusCode.OK)
                }

                val subId = sub.id
                val priceId = sub.items.data.getOrNull(0)?.price?.id
                    ?: run {
                        log.warn { "received stripe customer.subscription.updated event without a price id" }
                        return@post call.respond(HttpStatusCode.OK)
                    }

                val customerId = sub.customer
                val customer = Customer.retrieve(sub.customer)

                val user = customer.metadata[StripeClient.MetadataKeys.INDEX_USER_ID]?.let { userId -> userDao.get(userId.toIxId()) }
                    ?: run {
                        val configurationExceptionMessage = "Stripe customer missing required metadata key: ${StripeClient.MetadataKeys.INDEX_USER_ID}"

                        log.error { configurationExceptionMessage }
                        Sentry.captureException(ConfigurationException(configurationExceptionMessage))

                        userDao.getFromStripeCustomerId(customerId)
                    }

                // This should logically never occur
                if (user == null) {
                    log.error { "Couldn't find index user for stripe customer with id $customerId" }
                    return@post call.respond(HttpStatusCode.NotFound)
                }

                val userData = userDao.setStripeSubscriptionData(
                    id = user.id,
                    subscriptionId = subId,
                    priceId = priceId
                ) ?: return@post call.respond(HttpStatusCode.NotFound)

                log.info { "updated user stripe subscription data with new active subscription" }

                call.respond(HttpStatusCode.OK)

                emitWebsocketEventForUsers(
                    websocketEventManager = websocketEventManager,
                    type = WebsocketEventType.USER_UPDATED,
                    content = WebsocketEventContent.UserUpdateEventContent(userData.getResponseDto()),
                    users = listOf(userData.id)
                )

                // here we could also send a nice email with a bunch of info or properly setup stripe emails instead
            }
            "customer.subscription.updated" -> {
                val sub = stripeObject as Subscription

                // we only update the user info when the subscription is active (paid)
                if (sub.status != "active") {
                    log.info { "received non active stripe subscription webhook" }
                    call.respond(HttpStatusCode.OK)
                }

                val subId = sub.id
                val priceId = sub.items.data.getOrNull(0)?.price?.id
                    ?: run {
                        log.warn { "received stripe customer.subscription.updated event without a price id" }
                        return@post call.respond(HttpStatusCode.OK)
                    }

                val customerId = sub.customer
                val customer = Customer.retrieve(sub.customer)

                val user = customer.metadata[StripeClient.MetadataKeys.INDEX_USER_ID]?.let { userId -> userDao.get(userId.toIxId()) }
                    ?: run {
                        val configurationExceptionMessage = "Stripe customer missing required metadata key: ${StripeClient.MetadataKeys.INDEX_USER_ID}"

                        log.error { configurationExceptionMessage }
                        Sentry.captureException(ConfigurationException(configurationExceptionMessage))

                        userDao.getFromStripeCustomerId(customerId)
                    }

                // This should logically never occur
                if (user == null) {
                    log.error { "Couldn't find index user for stripe customer with id $customerId" }
                    return@post call.respond(HttpStatusCode.NotFound)
                }

                val userData = userDao.setStripeSubscriptionData(
                    id = user.id,
                    subscriptionId = subId,
                    priceId = priceId
                ) ?: return@post call.respond(HttpStatusCode.NotFound)

                log.info { "updated user stripe subscription data with new active subscription" }

                call.respond(HttpStatusCode.OK)

                emitWebsocketEventForUsers(
                    websocketEventManager = websocketEventManager,
                    type = WebsocketEventType.USER_UPDATED,
                    content = WebsocketEventContent.UserUpdateEventContent(userData.getResponseDto()),
                    users = listOf(userData.id)
                )
                // here we could also send a nice email with a bunch of info or properly setup stripe emails instead
            }
            "customer.subscription.deleted" -> {
                val sub = stripeObject as Subscription
                val subId = sub.id
                val customerId = sub.customer
                val customer = Customer.retrieve(sub.customer)

                val user = customer.metadata[StripeClient.MetadataKeys.INDEX_USER_ID]?.let { userId -> userDao.get(userId.toIxId()) }
                    ?: run {
                        val configurationExceptionMessage = "Stripe customer missing required metadata key: ${StripeClient.MetadataKeys.INDEX_USER_ID}"

                        log.error { configurationExceptionMessage }
                        Sentry.captureException(ConfigurationException(configurationExceptionMessage))

                        userDao.getFromStripeCustomerId(customerId)
                    }

                // This should logically never occur
                if (user == null) {
                    log.error { "Couldn't find index user for stripe customer with id $customerId" }
                    return@post call.respond(HttpStatusCode.NotFound)
                }

                if (user.stripe_subscription_id == subId) {
                    val userData = userDao.setStripeSubscriptionData(
                        id = user.id,
                        subscriptionId = null,
                        priceId = null
                    ) ?: return@post call.respond(HttpStatusCode.NotFound)

                    log.info { "removed user stripe subscription data" }

                    call.respond(HttpStatusCode.OK)

                    emitWebsocketEventForUsers(
                        websocketEventManager = websocketEventManager,
                        type = WebsocketEventType.USER_UPDATED,
                        content = WebsocketEventContent.UserUpdateEventContent(userData.getResponseDto()),
                        users = listOf(userData.id)
                    )
                } else {
                    call.respond(HttpStatusCode.OK)
                }
            }
            else -> call.respond(HttpStatusCode.OK)
        }
    }
}