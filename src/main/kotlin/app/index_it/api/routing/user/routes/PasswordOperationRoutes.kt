package app.index_it.api.routing.user.routes

import app.index_it.api.routing.user.PasswordForgottenRoute
import app.index_it.api.routing.user.ResetPasswordRoute
import app.index_it.core.clients.SendinblueClient
import app.index_it.core.logic.PasswordEncoder
import app.index_it.daos.auth.PasswordResetDao
import app.index_it.daos.user.UserDao
import app.index_it.daos.auth.UserSessionDao
import app.index_it.models.auth.PasswordResetRequestBody
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.passwordOperationRoutes() {
    get<PasswordForgottenRoute> { request ->
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

    post<ResetPasswordRoute> { request ->
        val passwordResetDto = PasswordResetDao.get(request.token)
            ?: return@post call.respond(HttpStatusCode.NotFound)

        val user = UserDao.get(passwordResetDto.user_id)
            ?: return@post call.respond(HttpStatusCode.NotFound)

        val newPassword = call.receive<PasswordResetRequestBody>().password
        val newPasswordHashed = PasswordEncoder.encode(newPassword)

        // If the user email wasn't verified before, now it can be considered verified
        UserDao.resetPassword(passwordResetDto.user_id, newPasswordHashed, !user.email_verified)

        // Invalidate all other user active sessions
        UserSessionDao.deleteAllSessionsOfUser(passwordResetDto.user_id)

        // Send notification email
        SendinblueClient.sendPasswordNotificationEmail(user.email)
        
        call.respond(HttpStatusCode.OK)
    }
}
