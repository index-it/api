package app.index.api.routing.auth.routes

import app.index.api.plugins.emitAnalyticsEvent
import app.index.api.routing.auth.LoginWithApple
import app.index.api.routing.auth.LoginWithGoogle
import app.index.core.clients.oauth.AppleOAuthClient
import app.index.core.clients.oauth.GoogleOAuthClient
import app.index.core.exceptions.AuthenticationException
import app.index.core.logic.AnalyticsEventManager
import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.newIxId
import app.index.data.daos.auth.UserSessionDao
import app.index.data.daos.user.UserDao
import app.index.data.models.analytics.AnalyticsEventData
import app.index.data.models.user.UserData
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject

/**
 * A user can register / sign-in with Google, Facebook and Apple
 * Different services where the user has the same email are all linked to the same account
 */
fun Route.oauthLoginRoutes() {
    val userDao by inject<UserDao>()
    val userSessionDao by inject<UserSessionDao>()
    val googleOAuthClient by inject<GoogleOAuthClient>()
    val appleOAuthClient by inject<AppleOAuthClient>()
    val analyticsEventManager by inject<AnalyticsEventManager>()

    /**
     * google oauth login
     *
     * the user needs to get an id token with google oauth and forward it to this endpoint to get authenticated via google
     *
     * @tag auth
     * @operationId login-with-google
     * @query token_id the id token received from google
     * @response 200 user authenticated and session created
     * @response 401 invalid id token
     * @response 405 user email not verified
     */
    get<LoginWithGoogle> {
        val userInfo = googleOAuthClient.getUserInfoFromIdTokenIfValid(it.token_id)
            ?: throw AuthenticationException()

        if (!userInfo.verifiedEmail) {
            return@get call.respond(HttpStatusCode.MethodNotAllowed)
        }

        // If the email is already registered then log them in into that account directly (even if the account wasn't registered with Google)
        var userG = userDao.getFromEmail(userInfo.email)
        val isFirstLogin = userG == null || !userG.emailVerified

        if (userG == null) {
            // Create the user in the db with a random id, the email gotten from Google, email verified to true
            userG = UserData(
                id = newIxId(),
                email = userInfo.email,
                passwordHash = null,
                emailVerified = true,
                creationTimestamp = DatetimeUtils.currentMillis(),
                creationSource = UserData.CreationSource.GOOGLE,
                has_pro = false
            )

            userDao.create(userG)
        } else if (!userG.emailVerified) {
            userDao.verifyEmail(userG.id)
        }

        // Create session
        val sessionId = userSessionDao.create(
            userId = userG.id,
            device = call.request.userAgent(),
            ip = call.request.origin.remoteAddress
        )

        call.sessions.set(sessionId)
        call.respond(userG.getResponseDto())

        if (isFirstLogin) {
            emitAnalyticsEvent(
                analyticsEventManager = analyticsEventManager,
                analyticsEventData = AnalyticsEventData.UserRegistrationEventData(
                    creation_source = userG.creationSource
                )
            )
        } else {
            emitAnalyticsEvent(
                analyticsEventManager = analyticsEventManager,
                analyticsEventData = AnalyticsEventData.UserLoginEventData(
                    user_id = userG.id,
                    login_source = UserData.CreationSource.GOOGLE,
                )
            )
        }
    }

    /**
     * apple oauth login
     *
     * the user needs to get an id token with apple oauth and forward it to this endpoint to get authenticated via apple
     *
     * @tag auth
     * @operationId login-with-apple
     * @query token_id the id token received from apple
     * @response 200 user authenticated and session created
     * @response 401 invalid id token
     * @response 405 user email not verified
     */
    get<LoginWithApple> {
        val userInfo = appleOAuthClient.getUserInfoFromIdTokenIfValid(it.token_id)
            ?: throw AuthenticationException()

        if (!userInfo.emailVerified) {
            return@get call.respond(HttpStatusCode.MethodNotAllowed)
        }

        // If the email is already registered then log them in into that account directly (even if the account wasn't registered with Google)
        var userG = userDao.getFromEmail(userInfo.email)
        val isFirstLogin = userG == null || !userG.emailVerified

        if (userG == null) {
            // Create the user in the db with a random id, the email gotten from Google, email verified to true
            userG = UserData(
                id = newIxId(),
                email = userInfo.email,
                passwordHash = null,
                emailVerified = true,
                creationTimestamp = DatetimeUtils.currentMillis(),
                creationSource = UserData.CreationSource.APPLE,
                has_pro = false
            )

            userDao.create(userG)
        } else if (!userG.emailVerified) {
            userDao.verifyEmail(userG.id)
        }

        // Create session
        val sessionId = userSessionDao.create(
            userId = userG.id,
            device = call.request.userAgent(),
            ip = call.request.origin.remoteAddress
        )

        call.sessions.set(sessionId)
        call.respond(userG.getResponseDto())

        if (isFirstLogin) {
            emitAnalyticsEvent(
                analyticsEventManager = analyticsEventManager,
                analyticsEventData = AnalyticsEventData.UserRegistrationEventData(
                    creation_source = userG.creationSource
                )
            )
        } else {
            emitAnalyticsEvent(
                analyticsEventManager = analyticsEventManager,
                analyticsEventData = AnalyticsEventData.UserLoginEventData(
                    user_id = userG.id,
                    login_source = UserData.CreationSource.APPLE,
                )
            )
        }
    }

    /*
    get<LoginWithFacebook>({
        tags = listOf("auth")
        operationId = "login-with-facebook"
        summary = "facebook oauth login"
        description = "the user needs to get an access token with facebook oauth and forward it to this endpoint to get authenticated via facebook"
        protected = false
        request {
            queryParameter<String>("accessToken") {
                description = "the access token received from facebook"
                required = true
                allowEmptyValue = false
                allowReserved = false
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "user authenticated and session created"
                header<String>(HttpHeaders.SetCookie) {
                    description = "header that sets the session cookie via `SetCookie`"
                }
            }
            HttpStatusCode.Unauthorized to {
                description = "invalid access token"
            }
        }
    }) {
        // Get the email from the token
        // The email is already verified by facebook
        val userInfo = FacebookOAuthClient.getUserInfo(it.accessToken)
            ?: throw AuthenticationException()

        // If the email is already registered then log them in into that account directly (even if the account wasn't registered with Google)
        var userF = UserDao.getFromEmail(userInfo.email)

        if (userF == null) {
            // Create the user in the db with a random id, the email gotten from Google, email verified to true
            userF = UserDto(
                id = newIxId(),
                email = userInfo.email,
                passwordHash = null,
                emailVerified = true,
                creationTimestamp = DatetimeUtils.currentMillis(),
                creationSource = UserDto.CreationSource.FACEBOOK
            )

            UserDao.create(userF)
        } else if (!userF.emailVerified) {
            UserDao.verifyEmail(userF.id)
        }

        // Create session
        val sessionId = UserSessionDao.create(userF.id, call.request.userAgent(), call.request.origin.remoteAddress)

        call.sessions.set(sessionId)
        call.respond(HttpStatusCode.OK)
    }
     */
}
