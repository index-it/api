package app.index_it.api.routing.admin

import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.adminRoutes() {
    authenticate("auth-bearer-admin") {
        route("/admin") {

        }
    }
}
