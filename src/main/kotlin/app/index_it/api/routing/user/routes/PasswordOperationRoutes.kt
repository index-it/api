package app.index_it.api.routing.user.routes

import app.index_it.api.routing.user.PasswordForgottenRoute
import app.index_it.api.routing.user.RequestPasswordChangeRoute
import io.ktor.server.resources.*
import io.ktor.server.routing.*

fun Route.passwordOperationRoutes() {
    get<PasswordForgottenRoute> {

    }

    get<RequestPasswordChangeRoute> {

    }
}
