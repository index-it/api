package app.index.api.routing.auth.routes

import app.index.shared.core.logic.DatetimeUtils
import app.index.shared.core.logic.PasswordEncoder
import app.index.shared.core.typedId.newIxId
import app.index.api.core.logic.usecases.EmailVerificationUseCase
import app.index.api.core.logic.usecases.UserAuthUseCase
import app.index.shared.core.data.daos.user.UserDao
import app.index.shared.core.data.models.auth.RegistrationCredentials
import app.index.shared.core.data.models.user.UserData
import app.index.api.routing.auth.RegisterRoute
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
     * Register with an email and password.
     *
     * Tag: auth
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
