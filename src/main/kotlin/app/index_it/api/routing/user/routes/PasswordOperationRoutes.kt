package app.index_it.api.routing.user.routes

import app.index_it.api.plugins.emitRabbitMqWebsocketEvent
import app.index_it.api.routing.user.PasswordForgottenRoute
import app.index_it.api.routing.user.ResetPasswordRoute
import app.index_it.core.clients.SendinblueClient
import app.index_it.core.logic.PasswordEncoder
import app.index_it.core.logic.websocket.WebsocketConnectionsManager
import app.index_it.data.daos.auth.PasswordResetDao
import app.index_it.data.daos.auth.UserSessionDao
import app.index_it.data.daos.user.UserDao
import app.index_it.data.models.auth.PasswordResetRequestBody
import app.index_it.data.models.websocket.RabbitMqWebsocketEventType
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.passwordOperationRoutes() {
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
        val user = UserDao.getFromEmail(request.email)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        if (PasswordResetDao.isRateLimited(user.id))
            return@get call.respond(HttpStatusCode.TooManyRequests)

        val sentEmail = PasswordResetDao.createAndSend(user)

        if (sentEmail)
            call.respond(HttpStatusCode.OK)
        else
            call.respond(HttpStatusCode.InternalServerError)
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
            HttpStatusCode.NotFound to {
                description = "something went wrong"
            }
        }
    }) { request ->
        val passwordResetDto = PasswordResetDao.get(request.token)
            ?: return@post call.respond(HttpStatusCode.NotFound)

        val user = UserDao.get(passwordResetDto.userId)
            ?: return@post call.respond(HttpStatusCode.NotFound)

        val newPassword = call.receive<PasswordResetRequestBody>().password
        val newPasswordHashed = PasswordEncoder.encode(newPassword)

        // If the user email wasn't verified before, now it can be considered verified
        UserDao.resetPassword(passwordResetDto.userId, newPasswordHashed, !user.emailVerified)

        // Invalidate all other user websocket connections
        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.CLOSE_ALL_CLIENT_CONNECTIONS, null)
        WebsocketConnectionsManager.closeAllSessionsOfUser(passwordResetDto.userId)

        // Invalidate all other user active sessions
        UserSessionDao.deleteAllSessionsOfUser(passwordResetDto.userId)

        // Send notification email
        SendinblueClient.sendPasswordResetSuccessEmail(user.email)

        call.respond(HttpStatusCode.OK)
    }
}
