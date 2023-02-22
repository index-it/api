package app.index_it.api.routing.auth.routes

import app.index_it.api.routing.auth.LoginWithApple
import app.index_it.api.routing.auth.LoginWithFacebook
import app.index_it.api.routing.auth.LoginWithGoogle
import app.index_it.core.clients.oauth.AppleOAuthClient
import app.index_it.core.clients.oauth.FacebookOAuthClient
import app.index_it.core.clients.oauth.GoogleOAuthClient
import app.index_it.core.exceptions.AuthenticationException
import app.index_it.daos.UserDao
import app.index_it.daos.UserSessionDao
import app.index_it.models.user.UserDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.date.*

/**
 * A user can register / sign-in with Google, Facebook and Apple
 * Different services where the user has the same email are all linked to the same account
 */
fun Route.oauthLoginRoutes() {
    get<LoginWithGoogle> {
        // Exchange the code for the token
        val token = GoogleOAuthClient.exchangeCodeForToken(it.code)
            ?: throw AuthenticationException()

        // Get the email the token
        val userInfo = GoogleOAuthClient.getUserInfo(token)
            ?: return@get call.respond(HttpStatusCode.InternalServerError)

        if (!userInfo.verifiedEmail)
            return@get call.respond(HttpStatusCode.MethodNotAllowed)

        // If the email is already registered then log them in into that account directly (even if the account wasn't registered with Google)
        var userG = UserDao.getFromEmail(userInfo.email)

        if (userG == null) {
            // Create the user in the db with a random id, the email gotten from Google, email verified to true
            userG = UserDto(
                email = userInfo.email,
                password_hash = null,
                creation_timestamp = getTimeMillis(),
                creation_source = UserDto.CreationSource.GOOGLE
            )

            UserDao.create(userG)
        }

        // Create session
        val sessionId = UserSessionDao.create(userG.id, call.request.userAgent(), call.request.origin.remoteAddress)

        call.sessions.set(sessionId)
        call.respond(HttpStatusCode.OK)
    }

    get<LoginWithApple> {
        // Exchange the code for the token
        val userInfo = AppleOAuthClient.exchangeCodeAndGetUserInfo(it.code)
            ?: throw AuthenticationException()

        if (userInfo.isPrivateEmail || !userInfo.verifiedEmail)
            return@get call.respond(HttpStatusCode.MethodNotAllowed)

        // If the email is already registered then log them in into that account directly (even if the account wasn't registered with Apple)
        var userA = UserDao.getFromEmail(userInfo.email)

        if (userA == null) {
            // Create the user in the db with a random id, the email gotten from Apple, email verified to true
            userA = UserDto(
                email = userInfo.email,
                password_hash = null,
                creation_timestamp = getTimeMillis(),
                creation_source = UserDto.CreationSource.APPLE
            )

            UserDao.create(userA)
        }

        // Create session
        val sessionId = UserSessionDao.create(userA.id, call.request.userAgent(), call.request.origin.remoteAddress)

        call.sessions.set(sessionId)
        call.respond(HttpStatusCode.OK)
    }

    get<LoginWithFacebook> {
        // Exchange the code for the token
        val token = FacebookOAuthClient.exchangeCodeForToken(it.code)
            ?: throw AuthenticationException()

        // Get the email the token
        val userInfo = FacebookOAuthClient.getUserInfo(token)
            ?: return@get call.respond(HttpStatusCode.InternalServerError)

        // If the email is already registered then log them in into that account directly (even if the account wasn't registered with Google)
        var userF = UserDao.getFromEmail(userInfo.email)

        if (userF == null) {
            // Create the user in the db with a random id, the email gotten from Google, email verified to true
            userF = UserDto(
                email = userInfo.email,
                password_hash = null,
                creation_timestamp = getTimeMillis(),
                creation_source = UserDto.CreationSource.FACEBOOK
            )

            UserDao.create(userF)
        }

        // Create session
        val sessionId = UserSessionDao.create(userF.id, call.request.userAgent(), call.request.origin.remoteAddress)

        call.sessions.set(sessionId)
        call.respond(HttpStatusCode.OK)
    }
}
