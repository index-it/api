package app.index_it.api.routing.auth.routes

import app.index_it.Env
import app.index_it.api.plugins.AuthenticationMethods
import app.index_it.api.plugins.UserIdPrincipalForEmailVerificationAuth
import app.index_it.api.routing.auth.IsEmailVerifiedRoute
import app.index_it.api.routing.auth.SendVerificationEmailRoute
import app.index_it.api.routing.auth.VerifyEmailRoute
import app.index_it.data.daos.auth.EmailVerificationDao
import app.index_it.data.daos.user.UserDao
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.emailVerificationRoutes() {
    authenticate(AuthenticationMethods.EMAIL_VERIFICATION_FORM_AUTH) {

        /**
         * Sends an email to verify the user email
         */
        post<SendVerificationEmailRoute>({
            tags = listOf("auth")
            operationId = "send-verification-email"
            summary = "sends a verification email to the email of the user"
            description = "sends a verification email to the user, unless the user is rate limited on this endpoint"
            protected = false
            request {
                body("EmailVerificationAuthForm") {
                    description = "email and password with which the user registered"
                    mediaType(ContentType.Application.FormUrlEncoded)
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "email sent (unless the email was already verified)"
                }
                HttpStatusCode.Forbidden to {
                    description = "invalid form credentials"
                }
            }
        }) {
            val userDto = call.principal<UserIdPrincipalForEmailVerificationAuth>()?.id?.let {
                UserDao.get(it)
            } ?: return@post call.respond(HttpStatusCode.Forbidden)

            if (userDto.emailVerified)
                return@post call.respond(HttpStatusCode.OK)

            if (app.index_it.data.daos.auth.EmailVerificationDao.isRateLimited(userDto.id))
                return@post call.respond(HttpStatusCode.TooManyRequests)

            val emailSent = app.index_it.data.daos.auth.EmailVerificationDao.createAndSend(userDto)
            if (emailSent)
                call.respond(HttpStatusCode.Created)
            else
                call.respond(HttpStatusCode.InternalServerError)
        }

        /**
         * Checks if an email has been verified
         */
        post<IsEmailVerifiedRoute>({
            tags = listOf("auth")
            operationId = "is-email-verified"
            summary = "tells if an email has been verified"
            protected = false
            request {
                body("EmailVerificationAuthForm") {
                    description = "email and password with which the user registered"
                    mediaType(ContentType.Application.FormUrlEncoded)
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "email is verified"
                }
                HttpStatusCode.Forbidden to {
                    description = "invalid form credentials"
                }
                HttpStatusCode.NotFound to {
                    description = "for sure not verified, might be not found neither"
                }
            }
        }) {
            val userDto = call.principal<UserIdPrincipalForEmailVerificationAuth>()?.id?.let {
                UserDao.get(it)
            } ?: return@post call.respond(HttpStatusCode.Forbidden)

            if (userDto.emailVerified)
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.NotFound)
        }
    }

    /**
     * Uses the code sent in the email inbox of the user to verify its email
     */
    get<VerifyEmailRoute>({
        tags = listOf("auth")
        operationId = "verify-email"
        summary = "verifies the email via the token received in the email"
        protected = false
        request {
            queryParameter<String>("token") {
                description = "encoded verification token"
                required = true
                allowEmptyValue = false
                allowReserved = false
            }
        }
        response {
            HttpStatusCode.TemporaryRedirect to {
                description = "redirects to either a success or failure page"
            }
            HttpStatusCode.BadRequest to {
                description = "token is expired likely"
            }
        }
    }) { request ->
        val emailVerificationDto = EmailVerificationDao.get(request.token)
            ?: return@get call.respondRedirect(Env.email_verification_error_url)

        val userDto = UserDao.get(emailVerificationDto.userId)
            ?: return@get call.respond(HttpStatusCode.BadRequest)

        // Check if user is already verified
        if (userDto.emailVerified)
            return@get call.respondRedirect(Env.email_verification_success_url)

        UserDao.verifyEmail(userDto.id)
        app.index_it.data.daos.auth.EmailVerificationDao.deleteAll(userDto.id)
        call.respondRedirect(Env.email_verification_success_url)
    }
}
