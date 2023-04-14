package app.index_it.api.routing.admin

import app.index_it.api.plugins.AuthenticationMethods
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.adminRoutes() {
    authenticate(AuthenticationMethods.adminBearerAuth) {
        route("/admin") {

        }
    }
}
