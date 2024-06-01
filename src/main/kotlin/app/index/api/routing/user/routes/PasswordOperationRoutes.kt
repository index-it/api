package app.index.api.routing.user.routes

import app.index.api.plugins.emitWebsocketEventForCurrentSessionUser
import app.index.api.routing.user.PasswordForgottenRoute
import app.index.api.routing.user.ResetPasswordRoute
import app.index.core.clients.BrevoClient
import app.index.core.logic.PasswordEncoder
import app.index.core.logic.usecases.PasswordResetUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.auth.PasswordResetDao
import app.index.data.daos.auth.UserSessionDao
import app.index.data.daos.user.UserDao
import app.index.data.models.auth.PasswordResetRequestBody
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.passwordOperationRoutes() {
    val userDao by inject<UserDao>()
    val userSessionDao by inject<UserSessionDao>()
    val passwordResetDao by inject<PasswordResetDao>()
    val passwordEncoder by inject<PasswordEncoder>()
    val brevoClient by inject<BrevoClient>()
    val websocketEventManager by inject<WebsocketEventManager>()

    get<PasswordForgottenRoute>({
        tags = listOf("auth")
        operationId = "password-forgotten"
        summary = "request a password reset email"
        description = "will send an email with instructions on how to reset the password, this is subject to rate limits as with all email operations"
        protected = false
        request {
            queryParameter<String>("email") {
                description = "the encoded email of the user"
                example = "sample%40mail.com"
                required = true
                allowEmptyValue = false
                allowReserved = false
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "email sent"
            }
            HttpStatusCode.NotFound to {
                description = "something went wrong"
            }
            HttpStatusCode.TooManyRequests to {
                description = "action rate limited"
            }
        }
    }) { request ->
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

    post<ResetPasswordRoute>({
        tags = listOf("auth")
        operationId = "reset-password"
        summary = "reset the password via an auth token"
        description = "a user can reset its password via a token that is sent via email when he requests it"
        protected = false
        request {
            queryParameter<String>("token") {
                description = "reset token"
                required = true
                allowEmptyValue = false
                allowReserved = false
            }
            body<PasswordResetRequestBody> {
                description = "contains the new password"
                required = true
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "password reset"
            }
            HttpStatusCode.BadRequest to {
                description = "password doesn't conform to rules, see response message"
            }
            HttpStatusCode.NotFound to {
                description = "something went wrong"
            }
        }
    }) { request ->
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
        emitWebsocketEventForCurrentSessionUser(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.USER_AUTH_SESSIONS_INVALIDATED,
            content = WebsocketEventContent.EmptyEventContent
        )

        // Invalidate all other user active sessions
        userSessionDao.deleteAllOfUser(passwordResetDto.userId)

        // Send notification email
        brevoClient.sendPasswordResetSuccessEmail(user.email)

        call.respond(HttpStatusCode.OK)
    }
}
