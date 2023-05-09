package app.index_it.api.routing.auth

import app.index_it.api.routing.auth.routes.*
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
class LoginWithGoogle(val tokenId: String)

@Resource("/login-with-apple")
class LoginWithApple(val code: String)

@Resource("/login-with-facebook")
class LoginWithFacebook(val code: String)


fun Route.authRoutes() {
    welcomeActionRoute()
    registerRoute()
    emailVerificationRoutes()
    loginRoute()
    oauthLoginRoutes()
}
