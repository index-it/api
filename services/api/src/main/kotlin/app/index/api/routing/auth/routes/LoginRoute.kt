package app.index.api.routing.auth.routes

import app.index.shared.core.exceptions.AuthenticationException
import app.index.api.core.logic.AnalyticsEventManager
import app.index.shared.core.logic.PasswordEncoder
import app.index.shared.core.data.daos.auth.UserSessionDao
import app.index.shared.core.data.daos.user.UserDao
import app.index.shared.core.data.models.analytics.AnalyticsEventData
import app.index.shared.core.data.models.auth.LoginCredentialsData
import app.index.shared.core.data.models.user.UserData
import app.index.api.plugins.emitAnalyticsEvent
import app.index.api.routing.auth.LoginRoute
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject

fun Route.loginRoute() {
    val userDao by inject<UserDao>()
    val userSessionDao by inject<UserSessionDao>()
    val passwordEncoder by inject<PasswordEncoder>()
    val analyticsEventManager by inject<AnalyticsEventManager>()

    /**
     * Login and create a session.
     *
     * Tag: auth
     */
    post<LoginRoute> {
        val loginData = call.receive<LoginCredentialsData>()
        val user = userDao.getFromEmail(loginData.email)
            ?: throw AuthenticationException()

        if (user.passwordHash == null) {
            throw AuthenticationException()
        }

        if (!passwordEncoder.matches(loginData.password, user.passwordHash!!)) {
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
