package app.index.api.routing.auth.routes

import app.index.shared.core.config.BrevoConfig
import app.index.api.core.logic.AnalyticsEventManager
import app.index.api.core.logic.usecases.EmailVerificationUseCase
import app.index.shared.core.data.daos.auth.EmailVerificationDao
import app.index.shared.core.data.daos.user.UserDao
import app.index.shared.core.data.models.analytics.AnalyticsEventData
import app.index.api.plugins.AuthenticationMethods
import app.index.api.plugins.UserIdPrincipalForEmailVerificationAuth
import app.index.api.plugins.emitAnalyticsEvent
import app.index.api.routing.auth.IsEmailVerifiedRoute
import app.index.api.routing.auth.SendVerificationEmailRoute
import app.index.api.routing.auth.VerifyEmailRoute
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.emailVerificationRoutes() {
    val userDao by inject<UserDao>()
    val emailVerificationDao by inject<EmailVerificationDao>()
    val analyticsEventManager by inject<AnalyticsEventManager>()

    authenticate(AuthenticationMethods.EMAIL_VERIFICATION_FORM_AUTH) {
        /**
         * Sends a verification email to the email of the user.
         *
         * Tag: auth
         *
         * Security: form
         */
        post<SendVerificationEmailRoute> {
            val userDto = call.principal<UserIdPrincipalForEmailVerificationAuth>()?.id?.let {
                userDao.get(it)
            } ?: return@post call.respond(HttpStatusCode.Forbidden)

            if (userDto.emailVerified) {
                return@post call.respond(HttpStatusCode.OK)
            }

            if (emailVerificationDao.isUserRateLimited(userDto.id)) {
                return@post call.respond(HttpStatusCode.TooManyRequests)
            }

            val emailSent = EmailVerificationUseCase.createAndSend(userDto)

            if (emailSent) {
                call.respond(HttpStatusCode.Created)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        /**
         * Tells if an email has been verified.
         *
         * Tag: auth
         *
         * Security: form
         */
        post<IsEmailVerifiedRoute> {
            val userDto =
                call.principal<UserIdPrincipalForEmailVerificationAuth>()?.id?.let {
                    userDao.get(it)
                } ?: return@post call.respond(HttpStatusCode.Forbidden)

            if (userDto.emailVerified) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }

    /**
     * Verifies the email via the token received in the email.
     *
     * Tag: auth
     */
    get<VerifyEmailRoute> { request ->
        val emailVerificationDto = emailVerificationDao.get(request.token)
            ?: return@get call.respondRedirect(BrevoConfig.emailVerificationErrorUrl)

        val userDto = userDao.get(emailVerificationDto.userId)
            ?: return@get call.respond(HttpStatusCode.BadRequest)

        // Check if user is already verified
        if (userDto.emailVerified) {
            return@get call.respondRedirect(BrevoConfig.emailVerificationSuccessUrl)
        }

        userDao.verifyEmail(userDto.id)
        emailVerificationDao.deleteAllOfUser(userDto.id)
        call.respondRedirect(BrevoConfig.emailVerificationSuccessUrl)

        emitAnalyticsEvent(
            analyticsEventManager = analyticsEventManager,
            analyticsEventData = AnalyticsEventData.UserRegistrationEventData(
                creation_source = userDto.creationSource
            )
        )
    }
}
