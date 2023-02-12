package app.index_it.api.routing.auth.routes

import app.index_it.api.routing.auth.LoginWithApple
import app.index_it.api.routing.auth.LoginWithGoogle
import app.index_it.core.clients.oauth.AppleOAuthClient
import app.index_it.core.clients.oauth.GoogleOAuthClient
import app.index_it.core.exceptions.AuthenticationException
import app.index_it.daos.UserDao
import app.index_it.daos.UserSessionDao
import app.index_it.models.user.UserDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.get
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
        var user = UserDao.getFromEmail(userInfo.email)

        if (user == null) {
            // Create the user in the db with a random id, the email gotten from Google, email verified to true
            user = UserDto(
                email = userInfo.email,
                password_hash = null,
                creation_timestamp = getTimeMillis(),
                creation_source = UserDto.CreationSource.GOOGLE
            )

            UserDao.create(user)
        }

        // Create session
        val sessionId = UserSessionDao.create(user.id)

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
        var user = UserDao.getFromEmail(userInfo.email)

        if (user == null) {
            // Create the user in the db with a random id, the email gotten from Apple, email verified to true
            user = UserDto(
                email = userInfo.email,
                password_hash = null,
                creation_timestamp = getTimeMillis(),
                creation_source = UserDto.CreationSource.APPLE
            )

            // Why do I need to assert here??
            UserDao.create(user!!)
        }

        // Create session
        val sessionId = UserSessionDao.create(user!!.id)

        call.sessions.set(sessionId)
        call.respond(HttpStatusCode.OK)
    }
}
