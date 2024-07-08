package app.index.api.routing.stripe

import app.index.api.plugins.AuthenticationMethods
import app.index.api.routing.stripe.routes.proSubscriptionRoute
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

@Resource("/pro")
class ProRoute {
    @Resource("/subscription")
    class SubscriptionRoute(val parent: ProRoute) {
        @Resource("/create")
        class CreateRoute(val parent: SubscriptionRoute, val price_id: String)

        @Resource("/cancel")
        class CancelRoute(val parent: SubscriptionRoute)
    }
}

fun Route.proRoutes() {
    authenticate(AuthenticationMethods.USER_SESSION_AUTH) {
        proSubscriptionRoute()
    }
}