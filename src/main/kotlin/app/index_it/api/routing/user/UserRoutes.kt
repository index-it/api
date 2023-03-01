package app.index_it.api.routing.user

import app.index_it.api.routing.user.routes.logoutRoute
import app.index_it.api.routing.user.routes.meRoutes
import app.index_it.api.routing.user.routes.passwordOperationRoutes
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*

@Resource("/logout")
class LogoutRoute

@Resource("/password-forgotten")
class PasswordForgottenRoute(val email: String)

@Resource("/reset-password")
class ResetPasswordRoute(val token: String)

@Resource("/me")
class MeRoute

fun Route.userRoutes() {
    passwordOperationRoutes()

    authenticate("auth-user-session") {
        logoutRoute()
        meRoutes()
    }
}
