package app.index_it.api.routing.auth.routes

import app.index_it.api.routing.auth.WelcomeActionRoute
import app.index_it.core.logic.usecases.UserAuthUseCase
import app.index_it.daos.user.UserDao
import app.index_it.models.auth.WelcomeAction
import app.index_it.models.auth.WelcomeActionResponse
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.welcomeActionRoute() {
    /**
     * The auth flow starts by determining the welcome action.
     * Depending on the email, a user can either register if there is no account associated with that email
     * or log in if there is.
     * To log in the user must have verified the email address.
     */
    get<WelcomeActionRoute> { request ->
        val userDto = UserDao.getFromEmail(request.email)

        val action = if (userDto == null || UserAuthUseCase.isIncompleteAccountOutdated(userDto))
            WelcomeAction.REGISTER
        else
            WelcomeAction.LOGIN

        call.respond(WelcomeActionResponse(action))
    }
}
