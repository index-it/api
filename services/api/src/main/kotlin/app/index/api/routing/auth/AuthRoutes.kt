package app.index.api.routing.auth

import app.index.api.routing.auth.routes.*
import io.ktor.resources.*
import io.ktor.server.routing.*

@Resource("/welcome-action")
class WelcomeActionRoute(val email: String)

@Resource("/register")
class RegisterRoute

@Resource("/send-verification-email")
class SendVerificationEmailRoute

@Resource("/verify-email")
class VerifyEmailRoute(val token: String)

@Resource("/is-email-verified")
class IsEmailVerifiedRoute

@Resource("/login")
class LoginRoute

@Resource("/login-with-google")
class LoginWithGoogle(val token_id: String)

@Resource("/login-with-apple")
class LoginWithApple(val token_id: String)

fun Route.authRoutes() {
    welcomeActionRoute()
    registerRoute()
    emailVerificationRoutes()
    loginRoute()
    oauthLoginRoutes()
}
