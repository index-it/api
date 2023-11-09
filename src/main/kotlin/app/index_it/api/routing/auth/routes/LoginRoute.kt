package app.index_it.api.routing.auth.routes

import app.index_it.api.routing.auth.LoginRoute
import app.index_it.core.exceptions.AuthenticationException
import app.index_it.core.logic.PasswordEncoder
import app.index_it.data.daos.user.UserDao
import app.index_it.data.models.auth.LoginCredentials
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.loginRoute() {
    /**
     * Logs in a user using email and password
     */
    post<LoginRoute>({
        tags = listOf("auth")
        operationId = "login"
        summary = "login and create a session"
        protected = false
        request {
            body<LoginCredentials> {
                description = "email and password credentials"
                required = true
                example("sample-credentials", LoginCredentials("sample@mail.com", "verySecurePwd1234"))
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
                description = "invalid credentials"
            }
            HttpStatusCode.MethodNotAllowed to {
                description = "user email is not verified"
            }
        }
    }) {
        val loginData = call.receive<LoginCredentials>()
        val user = UserDao.getFromEmail(loginData.email)
            ?: throw AuthenticationException()

        if (user.passwordHash == null)
            throw AuthenticationException()

        if (!PasswordEncoder.matches(loginData.password, user.passwordHash))
            throw AuthenticationException()

        // User email must be verified
        if (!user.emailVerified)
            return@post call.respond(HttpStatusCode.MethodNotAllowed)

        val userSessionId = app.index_it.data.daos.auth.UserSessionDao.create(user.id, call.request.userAgent(), call.request.origin.remoteAddress)

        call.sessions.set(userSessionId)
        call.respond(HttpStatusCode.OK)
    }
}
