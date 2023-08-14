package app.index_it.api.routing.admin

import app.index_it.api.plugins.AuthenticationMethods
import app.index_it.api.routing.admin.routes.usersRoute
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

@Resource("/admin")
@Suppress("unused")
class AdminRoute {
    @Resource("users")
    class UsersRoute(val parent: AdminRoute = AdminRoute()) {
        @Resource("verify-email")
        class VerifyEmailRoute(val parent: UsersRoute = UsersRoute(), val email: String)
    }
}

fun Route.adminRoutes() {
    authenticate(AuthenticationMethods.adminBearerAuth) {
        usersRoute()
    }
}
