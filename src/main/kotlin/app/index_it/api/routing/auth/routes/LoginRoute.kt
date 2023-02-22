package app.index_it.api.routing.auth.routes

import app.index_it.api.routing.auth.LoginRoute
import app.index_it.core.exceptions.AuthenticationException
import app.index_it.core.logic.PasswordEncoder
import app.index_it.daos.UserDao
import app.index_it.daos.UserSessionDao
import app.index_it.models.auth.LoginCredentials
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.loginRoute() {
    /**
     * Logs in a user using email and password
     */
    post<LoginRoute> {
        val loginData = call.receive<LoginCredentials>()
        val user = UserDao.getFromEmail(loginData.email)
            ?: throw AuthenticationException()

        if (user.password_hash == null)
            throw AuthenticationException()

        if (!PasswordEncoder.matches(loginData.password, user.password_hash))
            throw AuthenticationException()

        // User email must be verified
        if (!user.email_verified)
            return@post call.respond(HttpStatusCode.MethodNotAllowed)

        val userSessionId = UserSessionDao.create(user.id, call.request.userAgent(), call.request.origin.remoteAddress)

        call.sessions.set(userSessionId)
        call.respond(HttpStatusCode.OK)
    }
}
