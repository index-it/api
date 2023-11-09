package app.index_it.api.routing.auth.routes

import app.index_it.api.routing.auth.LoginWithApple
import app.index_it.api.routing.auth.LoginWithFacebook
import app.index_it.api.routing.auth.LoginWithGoogle
import app.index_it.core.clients.oauth.AppleOAuthClient
import app.index_it.core.clients.oauth.FacebookOAuthClient
import app.index_it.core.clients.oauth.GoogleOAuthClient
import app.index_it.core.exceptions.AuthenticationException
import app.index_it.data.daos.auth.UserSessionDao
import app.index_it.data.daos.user.UserDao
import app.index_it.data.models.user.UserDto
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.date.*

/**
 * A user can register / sign-in with Google, Facebook and Apple
 * Different services where the user has the same email are all linked to the same account
 */
fun Route.oauthLoginRoutes() {
    get<LoginWithGoogle>({
        tags = listOf("auth")
        operationId = "login-with-google"
        summary = "google oauth login"
        description = "the user needs to get an id token with google oauth and forward it to this endpoint to get authenticated via google"
        protected = false
        request {
            queryParameter<String>("tokenId") {
                description = "the id token received from google"
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
                description = "invalid id token"
            }
            HttpStatusCode.MethodNotAllowed to {
                description = "user email not verified"
            }
        }
    }) {
        val userInfo = GoogleOAuthClient.getUserInfoFromIdTokenIfValid(it.tokenId)
            ?: throw AuthenticationException()

        if (!userInfo.verifiedEmail)
            return@get call.respond(HttpStatusCode.MethodNotAllowed)

        // If the email is already registered then log them in into that account directly (even if the account wasn't registered with Google)
        var userG = UserDao.getFromEmail(userInfo.email)

        if (userG == null) {
            // Create the user in the db with a random id, the email gotten from Google, email verified to true
            userG = UserDto(
                email = userInfo.email,
                passwordHash = null,
                emailVerified = true,
                creationTimestamp = getTimeMillis(),
                creationSource = UserDto.CreationSource.GOOGLE
            )

            UserDao.create(userG)
        } else if (!userG.emailVerified) {
            UserDao.verifyEmail(userG.id)
        }

        // Create session
        val sessionId = app.index_it.data.daos.auth.UserSessionDao.create(userG.id, call.request.userAgent(), call.request.origin.remoteAddress)

        call.sessions.set(sessionId)
        call.respond(HttpStatusCode.OK)
    }

    get<LoginWithApple>({
        tags = listOf("auth")
        operationId = "login-with-apple"
        summary = "apple oauth login"
        description = "the user needs to get a code with apple oauth and forward it to this endpoint to get authenticated via apple"
        protected = false
        request {
            queryParameter<String>("code") {
                description = "the code received from apple"
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
                description = "invalid code"
            }
        }
    }) {
        // Exchange the code for the token
        val userInfo = AppleOAuthClient.exchangeCodeAndGetUserInfo(it.code)
            ?: throw AuthenticationException()

        if (userInfo.isPrivateEmail || !userInfo.verifiedEmail)
            return@get call.respond(HttpStatusCode.MethodNotAllowed)

        // If the email is already registered then log them in into that account directly (even if the account wasn't registered with Apple)
        // TODO: Check if email is verified
        var userA = UserDao.getFromEmail(userInfo.email)

        if (userA == null) {
            // Create the user in the db with a random id, the email gotten from Apple, email verified to true
            userA = UserDto(
                email = userInfo.email,
                passwordHash = null,
                emailVerified = true,
                creationTimestamp = getTimeMillis(),
                creationSource = UserDto.CreationSource.APPLE
            )

            UserDao.create(userA)
        } else if (!userA.emailVerified) {
            UserDao.verifyEmail(userA.id)
        }

        // Create session
        val sessionId = app.index_it.data.daos.auth.UserSessionDao.create(userA.id, call.request.userAgent(), call.request.origin.remoteAddress)

        call.sessions.set(sessionId)
        call.respond(HttpStatusCode.OK)
    }

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
                email = userInfo.email,
                passwordHash = null,
                emailVerified = true,
                creationTimestamp = getTimeMillis(),
                creationSource = UserDto.CreationSource.FACEBOOK
            )

            UserDao.create(userF)
        } else if (!userF.emailVerified) {
            UserDao.verifyEmail(userF.id)
        }

        // Create session
        val sessionId = app.index_it.data.daos.auth.UserSessionDao.create(userF.id, call.request.userAgent(), call.request.origin.remoteAddress)

        call.sessions.set(sessionId)
        call.respond(HttpStatusCode.OK)
    }
}
