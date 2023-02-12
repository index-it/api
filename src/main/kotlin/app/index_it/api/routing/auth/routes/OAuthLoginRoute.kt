package app.index_it.api.routing.auth.routes

import app.index_it.api.routing.auth.LoginWithGoogle
import app.index_it.core.clients.GoogleOAuthClient
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

fun Route.oauthLoginRoutes() {
    get<LoginWithGoogle> {
        // Exchange the code for the token
        val token = GoogleOAuthClient.exchangeCodeForToken(it.code)
            ?: throw AuthenticationException()

        // Get the email the token
        val email = GoogleOAuthClient.getUserEmail(token)
            ?: return@get call.respond(HttpStatusCode.InternalServerError)

        // If
        // the email is already registered then log them in into that account directly (even if the account wasn't registered with Google)
        var user = UserDao.getFromEmail(email)

        if (user == null) {
            // Create the user in the db with a random id, the email gotten from Google, email verified to true
            user = UserDto(
                email = email,
                password_hash = null,
                creation_timestamp = getTimeMillis()
            )

            UserDao.create(user)
        } else {
            // Update email verification field if set to false
            if (!user.email_verified) {
                UserDao.verifyEmail(user.id)
            }
        }

        // Create session
        val sessionId = UserSessionDao.create(user.id)

        call.sessions.set(sessionId)
        call.respond(HttpStatusCode.OK)
    }
}
