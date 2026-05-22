package app.index.api.routing.pro

import app.index.api.plugins.AuthenticationMethods
import app.index.api.routing.pro.routes.proSubscriptionRoute
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

@Resource("/pro")
class ProRoute {
    @Resource("/subscription")
    class SubscriptionRoute(val parent: ProRoute) {
        @Resource("/restore")
        class RestoreRoute(val parent: SubscriptionRoute)
    }
}

fun Route.proRoutes() {
    authenticate(AuthenticationMethods.USER_SESSION_AUTH) {
        proSubscriptionRoute()
    }
}