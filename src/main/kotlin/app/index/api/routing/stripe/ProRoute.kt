package app.index.api.routing.stripe

import app.index.api.plugins.AuthenticationMethods
import app.index.api.routing.stripe.routes.proPromotionCodeRoute
import app.index.api.routing.stripe.routes.proSubscriptionRoute
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

@Resource("/pro")
class ProRoute {
    @Resource("/subscription")
    class SubscriptionRoute(val parent: ProRoute) {
        @Resource("/create")
        class CreateRoute(val parent: SubscriptionRoute, val price_id: String, val promotion_code: String? = null)

        @Resource("/restore")
        class RestoreRoute(val parent: SubscriptionRoute)

        @Resource("/cancel")
        class CancelRoute(val parent: SubscriptionRoute)
    }

    @Resource("/promotion-code")
    class PromotionCodeRoute(val parent: ProRoute) {
        @Resource("/validate")
        class ValidateRoute(val parent: PromotionCodeRoute, val promotion_code: String)
    }
}

fun Route.proRoutes() {
    authenticate(AuthenticationMethods.USER_SESSION_AUTH) {
        proSubscriptionRoute()
        proPromotionCodeRoute()
    }
}