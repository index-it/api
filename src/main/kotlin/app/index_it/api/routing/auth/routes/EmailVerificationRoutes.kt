package app.index_it.api.routing.auth.routes

import app.index_it.api.routing.auth.IsEmailVerifiedRoute
import app.index_it.api.routing.auth.SendVerificationEmailRoute
import app.index_it.api.routing.auth.VerifyEmailRoute
import app.index_it.daos.EmailVerificationDao
import app.index_it.daos.UserDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLDecoder

fun Route.emailVerificationRoutes() {
    authenticate("email-verification-auth") {

        rateLimit(RateLimitName("email-verification-rate-limiter")) {
            /**
             * Sends an email to verify the user email
             */
            get<SendVerificationEmailRoute> { request ->
                val email = withContext(Dispatchers.IO) {
                    URLDecoder.decode(request.email, "utf-8")
                }

                val user = UserDao.getFromEmail(email)
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

                if (user.email_verified)
                    return@get call.respond(HttpStatusCode.OK)

                val emailSent = EmailVerificationDao.createAndSend(user.email)
                if (emailSent)
                    call.respond(HttpStatusCode.Created)
                else
                    call.respond(HttpStatusCode.InternalServerError)
            }
        }

        /**
         * Uses the code sent in the email inbox of the user to verify its email
         */
        get<VerifyEmailRoute> { request ->
            val email = withContext(Dispatchers.IO) {
                URLDecoder.decode(request.email, "utf-8")
            }

            val userDto = UserDao.getFromEmail(email)
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            // Check if user is already verified
            if (userDto.email_verified)
                return@get call.respondRedirect("https://index-it.app/email-verified")

            val emailVerificationDto = EmailVerificationDao.get(request.code)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            if (request.code == emailVerificationDto.code) {
                UserDao.verifyEmail(userDto.id)
                EmailVerificationDao.delete(request.code)
                return@get call.respondRedirect("https://index-it.app/email-verified")
            } else
                return@get call.respondRedirect("https://index-it.app/invalid-email-verification-code")
        }


        /**
         * Checks if an email has been verified
         */
        get<IsEmailVerifiedRoute> { request ->
            val email = withContext(Dispatchers.IO) {
                URLDecoder.decode(request.email, "utf-8")
            }

            val userDto = UserDao.getFromEmail(email)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            if (userDto.email_verified)
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.NotFound)
        }
    }
}
