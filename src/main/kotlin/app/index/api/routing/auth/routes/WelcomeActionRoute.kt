package app.index.api.routing.auth.routes

import app.index.api.routing.auth.WelcomeActionRoute
import app.index.core.logic.usecases.UserAuthUseCase
import app.index.data.daos.user.UserDao
import app.index.data.models.auth.WelcomeAction
import app.index.data.models.auth.WelcomeActionResponse
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.welcomeActionRoute() {
    val userDao by inject<UserDao>()

    /**
     * The auth flow starts by determining the welcome action.
     * Depending on the email, a user can either register if there is no account associated with that email
     * or log in if there is.
     * To log in the user must have verified the email address.
     */
    /**
     * retrieve the required first step for a user auth flow
     *
     * this is used to initialize a user auth flow
     *
     * @tag auth
     * @operationId welcome-action
     * @query email the encoded email of the user
     * @response 200 welcome action found and sent
     */
    get<WelcomeActionRoute> { request ->
        val userDto = userDao.getFromEmail(request.email)

        val action =
            if (userDto == null || UserAuthUseCase.isIncompleteAccountOutdated(userDto)) {
                WelcomeAction.REGISTER
            } else {
                WelcomeAction.LOGIN
            }

        call.respond(WelcomeActionResponse(action))
    }
}
