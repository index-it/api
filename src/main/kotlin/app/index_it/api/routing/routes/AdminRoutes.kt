package app.index_it.api.routing.routes

import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.admin() {
    authenticate("auth-full-api-key") {
        // TODO: Admin routes
    }
}
