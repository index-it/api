package app.index.api.routing.auth.routes

import app.index.api.core.logic.usecases.UserAuthUseCase
import app.index.api.data.daos.user.UserDao
import app.index.shared.core.data.models.auth.WelcomeAction
import app.index.shared.core.data.models.auth.WelcomeActionResponse
import app.index.api.routing.auth.WelcomeActionRoute
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.welcomeActionRoute() {
    val userDao by inject<UserDao>()

    /**
     * The auth flow starts by determining the welcome action.
     *
     * Tag: auth
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
