package app.index_it.api.routing.auth

import app.index_it.api.plugins.UserSessionId
import app.index_it.core.exceptions.AuthenticationException
import app.index_it.core.logic.PasswordEncoder
import app.index_it.daos.EmailVerificationDao
import app.index_it.daos.UserDao
import app.index_it.daos.UserSessionDao
import app.index_it.models.user.AuthCredentials
import app.index_it.models.user.UserDto
import app.index_it.models.user.UserSessionDto
import app.index_it.models.user.WelcomeAction
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.date.*
import kotlinx.serialization.EncodeDefault
import java.net.URLDecoder
import java.util.*

@Resource("/welcome-action")
private class WelcomeActionRoute(val email: String?)

@Resource("/register")
private class RegisterRoute

@Resource("/send-verification-email")
private class SendVerificationEmailRoute(val email: String?)

@Resource("/verify-email")
private class VerifyEmailRoute(val code: String?, val email: String?)

@Resource("/is-email-verified")
private class IsEmailVerifiedRoute(val email: String?)

@Resource("/login")
private class LoginRoute



fun Route.auth() {
    /**
     * The auth flow starts by determining the welcome action.
     * Depending on the email, a user can either register if there is no account associated with that email
     * or log in if there is.
     * To log in the user must have verified the email address.
     */
    get<WelcomeActionRoute> { request ->
        val email = request.email?.let { URLDecoder.decode(it, "utf-8") }
            ?: return@get call.respond(HttpStatusCode.BadRequest)

        val userDto = UserDao.getFromEmail(email)

        val action = if (userDto == null)
            WelcomeAction.REGISTER
        else if (!userDto.email_verified)
            WelcomeAction.VERIFY_EMAIL
        else
            WelcomeAction.LOGIN

        call.respondText(action.name, ContentType.Text.Plain, HttpStatusCode.OK)
    }

    /**
     * When a user registers, he needs to set an email and password,
     * and he will be able to log in into his account only once he has verified the email
     */
    post<RegisterRoute> {
        val signupData = call.receive<AuthCredentials>()
        if (UserDao.exists(signupData.email)) {
            call.respond(HttpStatusCode.Forbidden)
            return@post
        }

        val hashedPassword = PasswordEncoder.encode(signupData.password)
        val user = UserDto(
            email = signupData.email,
            password_hash = hashedPassword,
            creation_timestamp = getTimeMillis()
        )

        UserDao.create(user)

        val emailSent = EmailVerificationDao.createAndSend(user.email)

        if (emailSent)
            // User will need to verify its email
            call.respond(HttpStatusCode.OK)
        else
            call.respond(HttpStatusCode.Created)
    }

    authenticate("email-verification-auth") {

        rateLimit(RateLimitName("email-verification-rate-limiter")) {
            /**
             * Sends an email to verify the user email
             */
            get<SendVerificationEmailRoute> { request ->
                val email = request.email?.let { URLDecoder.decode(it, "utf-8") }
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

                val user = UserDao.getFromEmail(email)
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

                if (user.email_verified)
                    call.respond(HttpStatusCode.OK)

                // Maximum 3 verification emails every 60 minutes
                if (EmailVerificationDao.isRateLimited(email))
                    return@get call.respond(HttpStatusCode.TooManyRequests)

                val emailSent = EmailVerificationDao.createAndSend(user.email)
                if (emailSent)
                    call.respond(HttpStatusCode.OK)
                else
                    call.respond(HttpStatusCode.InternalServerError)
            }
        }

        /**
         * Uses the code sent in the email inbox of the user to verify its email
         */
        get<VerifyEmailRoute> { request ->
            val code = request.code
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            val email = request.email?.let { URLDecoder.decode(it, "utf-8") }
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val userDto = UserDao.getFromEmail(email)
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            // Check if user is already verified
            if (userDto.email_verified)
                return@get call.respondRedirect("https://index-it.app/email-verified")

            val emailVerificationDto = EmailVerificationDao.get(code)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            if (code == emailVerificationDto.code) {
                UserDao.verifyEmail(userDto.id)
                EmailVerificationDao.delete(code)
                return@get call.respondRedirect("https://index-it.app/email-verified")
            } else
                return@get call.respondRedirect("https://index-it.app/invalid-email-verification-code")
        }


        /**
         * Checks if an email has been verified
         */
        get<IsEmailVerifiedRoute> { request ->
            val email = request.email?.let { URLDecoder.decode(it, "utf-8") }
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val userDto = UserDao.getFromEmail(email)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            if (userDto.email_verified)
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.NotFound)
        }
    }

    /**
     * Logs in a user using email and password
     */
    post<LoginRoute> {
        val loginData = call.receive<AuthCredentials>()
        val user = UserDao.getFromEmail(loginData.email)
            ?: throw AuthenticationException()

        if (user.password_hash == null)
            throw AuthenticationException()

        if (!PasswordEncoder.matches(loginData.password, user.password_hash))
            throw AuthenticationException()

        // User email must be verified
        if (!user.email_verified)
            return@post call.respond(HttpStatusCode.MethodNotAllowed)

        val userSessionId = UserSessionId(getTimeMillis().toString() +  generateSessionId())

        val userSessionDto = UserSessionDto(userSessionId.session_id, getTimeMillis(), user.id)
        UserSessionDao.create(userSessionDto)

        call.sessions.set(userSessionId)
        call.respond(HttpStatusCode.OK)
    }
}
