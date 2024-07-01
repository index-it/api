package app.index.api.routing.stripe.routes

import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.stripe.StripeRoute
import app.index.core.exceptions.AuthenticationException
import app.index.core.logic.pro.ProManager
import app.index.data.daos.user.UserDao
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.stripeSubscriptionRoute() {
    val userDao by inject<UserDao>()
    val proManager by inject<ProManager>()

    get<StripeRoute.SubscriptionRoute.CreateRoute>({
        tags = listOf("stripe")
        operationId = "stripe-create-subscription"
        summary = "creates a payment intent that allows the user to purchase a subscription to the pro version"
        request {
            pathParameter<String>("plan_id") {
                required = true
                description = "the id of the plan the user wants to subscribe to"
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
                description = "user not logged in"
            }
            HttpStatusCode.BadRequest to {
                description = "invalid plan_id"
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()
        val user = userDao.get(userId)
            ?: throw AuthenticationException()
        val priceId = it.price_id

        val paymentIntentClientSecret = proManager.createSubscription(
            customerId = user.stripe_customer_id,
            userId = user.id,
            email =  user.email,
            priceId = priceId
        )

        call.respond(paymentIntentClientSecret)
    }

    get<StripeRoute.SubscriptionRoute.CancelRoute>({
        tags = listOf("stripe")
        operationId = "stripe-cancel-subscription"
        summary = "cancels the user subscription if he has an active one"
        response {
            HttpStatusCode.OK to {
                description = "subscription canceled"
            }
            HttpStatusCode.Unauthorized to {
                description = "user not logged in"
            }
            HttpStatusCode.NotFound to {
                description = "subscription with the provided id not found"
            }
            HttpStatusCode.MethodNotAllowed to {
                description = "user doesn't have an active subscription"
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()
        val user = userDao.get(userId)
            ?: throw AuthenticationException()

        if (user.stripe_subscription_id == null) {
            return@get call.respond(HttpStatusCode.MethodNotAllowed)
        }

        val canceled = proManager.cancelSubscription(user.stripe_subscription_id)

        if (!canceled) {
            return@get call.respond(HttpStatusCode.MethodNotAllowed)
        }

        call.respond(HttpStatusCode.OK)
    }
}