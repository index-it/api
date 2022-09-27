package app.index_it.plugins.routes

import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.admin() {
    authenticate("auth-full-api-key") {
        // TODO: Admin routes
    }
}
