package app.index.api.routing.auth.routes

import app.index.api.plugins.emitAnalyticsEvent
import app.index.api.routing.auth.LoginRoute
import app.index.core.exceptions.AuthenticationException
import app.index.core.logic.AnalyticsEventManager
import app.index.core.logic.PasswordEncoder
import app.index.data.daos.auth.UserSessionDao
import app.index.data.daos.user.UserDao
import app.index.data.models.analytics.AnalyticsEventData
import app.index.data.models.auth.LoginCredentialsData
import app.index.data.models.user.UserData
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject

fun Route.loginRoute() {
    val userDao by inject<UserDao>()
    val userSessionDao by inject<UserSessionDao>()
    val passwordEncoder by inject<PasswordEncoder>()
    val analyticsEventManager by inject<AnalyticsEventManager>()

    post<LoginRoute>({
        tags = listOf("auth")
        operationId = "login"
        summary = "login and create a session"
        protected = false
        request {
            body<LoginCredentialsData> {
                description = "email and password credentials"
                required = true
                example("sample-credentials", LoginCredentialsData("sample@mail.com", "verySecurePwd1234"))
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "user authenticated and session created"
                header<String>(HttpHeaders.SetCookie) {
                    description = "header that sets the session cookie via `SetCookie`"
                }
                body<UserData.UserResponseDto> {
                    description = "the user data excluding sensitive fields like the password"
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
        val loginData = call.receive<LoginCredentialsData>()
        val user = userDao.getFromEmail(loginData.email)
            ?: throw AuthenticationException()

        if (user.passwordHash == null) {
            throw AuthenticationException()
        }

        if (!passwordEncoder.matches(loginData.password, user.passwordHash)) {
            throw AuthenticationException()
        }

        // User email must be verified
        if (!user.emailVerified) {
            return@post call.respond(HttpStatusCode.MethodNotAllowed)
        }

        val userSessionId = userSessionDao.create(
            userId = user.id,
            device = call.request.userAgent(),
            ip = call.request.origin.remoteAddress
        )

        call.sessions.set(userSessionId)
        call.respond(user.getResponseDto())

        emitAnalyticsEvent(
            analyticsEventManager = analyticsEventManager,
            analyticsEventData = AnalyticsEventData.UserLoginEventData(
                user_id = user.id,
                login_source = UserData.CreationSource.NONE,
            )
        )
    }
}
