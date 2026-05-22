package app.index.api.routing.user.routes

import app.index.api.core.clients.BrevoClient
import app.index.api.core.logic.PasswordEncoder
import app.index.api.core.logic.usecases.PasswordResetUseCase
import app.index.api.core.logic.websocket.WebsocketEventManager
import app.index.api.core.logic.websocket.event.WebsocketEventContent
import app.index.api.core.logic.websocket.event.WebsocketEventType
import app.index.api.data.daos.auth.PasswordResetDao
import app.index.api.data.daos.auth.UserSessionDao
import app.index.api.data.daos.user.UserDao
import app.index.shared.core.data.models.auth.PasswordResetRequestBody
import app.index.api.routing.user.PasswordForgottenRoute
import app.index.api.routing.user.ResetPasswordRoute
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

private val log = KotlinLogging.logger {  }

fun Route.passwordOperationRoutes() {
    val userDao by inject<UserDao>()
    val userSessionDao by inject<UserSessionDao>()
    val passwordResetDao by inject<PasswordResetDao>()
    val passwordEncoder by inject<PasswordEncoder>()
    val brevoClient by inject<BrevoClient>()
    val websocketEventManager by inject<WebsocketEventManager>()

    /**
     * Request a password reset email.
     *
     * Tag: auth
     */
    get<PasswordForgottenRoute> { request ->
        val user = userDao.getFromEmail(request.email)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        if (passwordResetDao.isUserRateLimited(user.id)) {
            return@get call.respond(HttpStatusCode.TooManyRequests)
        }

        val sentEmail = PasswordResetUseCase.createAndSend(user)

        if (sentEmail) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    /**
     * Reset the password via an auth token.
     *
     * Tag: auth
     */
    post<ResetPasswordRoute> { request ->
        val passwordResetDto = passwordResetDao.get(request.token)
            ?: return@post call.respond(HttpStatusCode.NotFound)

        val user = userDao.get(passwordResetDto.userId)
            ?: return@post call.respond(HttpStatusCode.NotFound)

        val newPassword = call.receive<PasswordResetRequestBody>().password
        val newPasswordHashed = passwordEncoder.encode(newPassword)

        // If the user email wasn't verified before, now it can be considered verified
        userDao.resetPassword(
            id = passwordResetDto.userId,
            newPasswordHashed = newPasswordHashed,
            verifyEmail = true
        )

        // Invalidate all other user websocket connections
        try {
            websocketEventManager.emit(
                fromSessionId = null,
                fromUserId = user.id,
                eventType = WebsocketEventType.USER_AUTH_SESSIONS_INVALIDATED,
                eventData = WebsocketEventContent.EmptyEventContent,
                users = setOf(user.id),
                includeCurrentSession = false
            )
        } catch (e: Exception) {
            log.error(e) { "Error emitting websocket event for invalidating all user sessions after password reset" }
        }

        // Invalidate all other user active sessions
        userSessionDao.deleteAllOfUser(passwordResetDto.userId)

        // Send notification email
        brevoClient.sendPasswordResetSuccessEmail(user.email)

        call.respond(HttpStatusCode.OK)
    }
}
