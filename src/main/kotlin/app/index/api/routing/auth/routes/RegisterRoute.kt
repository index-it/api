package app.index.api.routing.auth.routes

import app.index.api.routing.auth.RegisterRoute
import app.index.core.logic.DatetimeUtils
import app.index.core.logic.PasswordEncoder
import app.index.core.logic.typedId.newIxId
import app.index.core.logic.usecases.EmailVerificationUseCase
import app.index.core.logic.usecases.UserAuthUseCase
import app.index.data.daos.user.UserDao
import app.index.data.models.auth.RegistrationCredentials
import app.index.data.models.user.UserData
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.registerRoute() {
    val userDao by inject<UserDao>()
    val passwordEncoder by inject<PasswordEncoder>()

    /**
     * When a user registers, he needs to set an email and password,
     * and he will be able to log in into his account only once he has verified the email
     */
    /**
     * register with an email and password
     *
     * a user can register with an email and password and will be able to login only after email verification
     *
     * @tag auth
     * @operationId register
     * @requestBody application/json email and password, password requirements: 8-100 chars with at least an uppercase, lowercase and number character
     * @response 200 user registered, an email has been sent for verification
     * @response 201 user registered, no email has been sent (for rate limiting reasons) but it still might be needed to verify the email
     * @response 403 can't register with those credentials
     */
    post<RegisterRoute> {
        val signupData = call.receive<RegistrationCredentials>()

        val existingUser = userDao.getFromEmail(signupData.email)

        if (existingUser != null) {
            if (UserAuthUseCase.isIncompleteAccountOutdated(existingUser)) {
                userDao.delete(existingUser.id)
            } else {
                call.respond(HttpStatusCode.Forbidden)
                return@post
            }
        }

        val hashedPassword = passwordEncoder.encode(signupData.password)
        val user = UserData(
            id = newIxId(),
            email = signupData.email,
            passwordHash = hashedPassword,
            emailVerified = false,
            creationTimestamp = DatetimeUtils.currentMillis(),
            creationSource = UserData.CreationSource.NONE,
            has_pro = false
        )

        userDao.create(user)

        val emailSent = EmailVerificationUseCase.createAndSend(user)

        if (emailSent) {
            // User will need to verify its email
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.Created)
        }
    }
}
