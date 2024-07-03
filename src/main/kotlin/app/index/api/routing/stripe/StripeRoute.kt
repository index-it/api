package app.index.api.routing.stripe

import app.index.api.plugins.AuthenticationMethods
import app.index.api.routing.stripe.routes.stripeSubscriptionRoute
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

@Resource("/pro")
class StripeRoute {
    @Resource("/subscription")
    class SubscriptionRoute(val parent: StripeRoute) {
        @Resource("/create")
        class CreateRoute(val parent: SubscriptionRoute, val price_id: String)

        @Resource("/cancel")
        class CancelRoute(val parent: SubscriptionRoute)
    }
}

fun Route.stripeRoutes() {
    authenticate(AuthenticationMethods.USER_SESSION_AUTH) {
        stripeSubscriptionRoute()
    }
}