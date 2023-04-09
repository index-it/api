package app.index_it.api.routing.auth.routes

import app.index_it.Env
import app.index_it.api.plugins.AuthenticationMethods
import app.index_it.api.plugins.UserIdPrincipalForEmailVerificationAuth
import app.index_it.api.routing.auth.IsEmailVerifiedRoute
import app.index_it.api.routing.auth.SendVerificationEmailRoute
import app.index_it.api.routing.auth.VerifyEmailRoute
import app.index_it.daos.auth.EmailVerificationDao
import app.index_it.daos.user.UserDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.emailVerificationRoutes() {
    authenticate(AuthenticationMethods.emailVerificationFormAuth) {

        /**
         * Sends an email to verify the user email
         */
        post<SendVerificationEmailRoute> {
            val userDto = call.principal<UserIdPrincipalForEmailVerificationAuth>()?.id?.let {
                UserDao.get(it)
            } ?: return@post call.respond(HttpStatusCode.Forbidden)

            if (userDto.email_verified)
                return@post call.respond(HttpStatusCode.OK)

            if (EmailVerificationDao.isRateLimited(userDto.id))
                return@post call.respond(HttpStatusCode.TooManyRequests)

            val emailSent = EmailVerificationDao.createAndSend(userDto)
            if (emailSent)
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.InternalServerError)
        }

        /**
         * Checks if an email has been verified
         */
        post<IsEmailVerifiedRoute> {
            val userDto = call.principal<UserIdPrincipalForEmailVerificationAuth>()?.id?.let {
                UserDao.get(it)
            } ?: return@post call.respond(HttpStatusCode.Forbidden)

            if (userDto.email_verified)
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.NotFound)
        }
    }

    /**
     * Uses the code sent in the email inbox of the user to verify its email
     */
    get<VerifyEmailRoute> { request ->
        val emailVerificationDto = EmailVerificationDao.get(request.token)
            ?: return@get call.respondRedirect(Env.email_verification_error_url)

        val userDto = UserDao.get(emailVerificationDto.user_id)
            ?: return@get call.respond(HttpStatusCode.BadRequest)

        // Check if user is already verified
        if (userDto.email_verified)
            return@get call.respondRedirect(Env.email_verification_success_url)

        UserDao.verifyEmail(userDto.id)
        EmailVerificationDao.deleteAll(userDto.id)
        call.respondRedirect(Env.email_verification_success_url)
    }
}
