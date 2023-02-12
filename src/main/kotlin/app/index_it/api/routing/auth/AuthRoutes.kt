package app.index_it.api.routing.auth

import app.index_it.api.routing.auth.routes.emailVerificationRoutes
import app.index_it.api.routing.auth.routes.loginRoute
import app.index_it.api.routing.auth.routes.registerRoute
import app.index_it.api.routing.auth.routes.welcomeActionRoute
import app.index_it.models.user.*
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.date.*
import java.util.*

@Resource("/welcome-action")
class WelcomeActionRoute(val email: String)

@Resource("/register")
class RegisterRoute

@Resource("/send-verification-email")
class SendVerificationEmailRoute(val email: String)

@Resource("/verify-email")
class VerifyEmailRoute(val code: String, val email: String)

@Resource("/is-email-verified")
class IsEmailVerifiedRoute(val email: String)

@Resource("/login")
class LoginRoute


fun Route.auth() {
    welcomeActionRoute()
    registerRoute()
    emailVerificationRoutes()
    loginRoute()
}
