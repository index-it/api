package app.index_it.api.routing.admin

import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.admin() {
    authenticate("auth-bearer-admin") {
        route("/admin") {

        }
    }
}
