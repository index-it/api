package app.index_it.api.routing.auth.routes

import app.index_it.api.routing.auth.RegisterRoute
import app.index_it.core.logic.PasswordEncoder
import app.index_it.daos.EmailVerificationDao
import app.index_it.daos.UserDao
import app.index_it.models.auth.RegistrationCredentials
import app.index_it.models.user.UserDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.date.*
import kotlin.time.Duration.Companion.days

fun Route.registerRoute() {
    /**
     * When a user registers, he needs to set an email and password,
     * and he will be able to log in into his account only once he has verified the email
     */
    post<RegisterRoute> {
        val signupData = call.receive<RegistrationCredentials>()

        val existingUser = UserDao.getFromEmail(signupData.email)

        if (existingUser != null) {
            if (!existingUser.email_verified && (getTimeMillis() - existingUser.creation_timestamp) > 7.days.inWholeMilliseconds) {
                UserDao.delete(existingUser.id)
            } else {
                call.respond(HttpStatusCode.Forbidden)
                return@post
            }
        }

        val hashedPassword = PasswordEncoder.encode(signupData.password)
        val user = UserDto(
            email = signupData.email,
            password_hash = hashedPassword,
            email_verified = false,
            creation_timestamp = getTimeMillis(),
            creation_source = UserDto.CreationSource.NONE
        )

        UserDao.create(user)

        val emailSent = EmailVerificationDao.createAndSend(user)

        if (emailSent)
        // User will need to verify its email
            call.respond(HttpStatusCode.OK)
        else
            call.respond(HttpStatusCode.Created)
    }
}
