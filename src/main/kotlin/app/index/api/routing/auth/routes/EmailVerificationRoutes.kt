package app.index.api.routing.auth.routes

import app.index.api.plugins.AuthenticationMethods
import app.index.api.plugins.UserIdPrincipalForEmailVerificationAuth
import app.index.api.plugins.emitAnalyticsEvent
import app.index.api.routing.auth.IsEmailVerifiedRoute
import app.index.api.routing.auth.SendVerificationEmailRoute
import app.index.api.routing.auth.VerifyEmailRoute
import app.index.config.BrevoConfig
import app.index.core.logic.AnalyticsEventManager
import app.index.core.logic.usecases.EmailVerificationUseCase
import app.index.data.daos.auth.EmailVerificationDao
import app.index.data.daos.user.UserDao
import app.index.data.models.analytics.AnalyticsEventData
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
         * sends a verification email to the email of the user
         *
         * sends a verification email to the user, unless the user is rate limited on this endpoint
         *
         * Tag: auth
         * @operationId send-verification-email
         * @requestBody application/x-www-form-urlencoded email and password with which the user registered
         * @response 200 email already verified
         * @response 201 email sent
         * @response 403 invalid form credentials
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
         * tells if an email has been verified
         *
         * @tag auth
         * @operationId is-email-verified
         * @requestBody application/x-www-form-urlencoded email and password with which the user registered
         * @response 200 email is verified
         * @response 403 invalid form credentials
         * @response 404 for sure not verified, might be not found neither
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
     * verifies the email via the token received in the email
     *
     * @tag auth
     * @operationId verify-email
     * @query token encoded verification token
     * @response 307 redirects to either a success or failure page
     * @response 400 token is likely expired
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
