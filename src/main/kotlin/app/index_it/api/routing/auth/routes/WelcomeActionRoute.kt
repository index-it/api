package app.index_it.api.routing.auth.routes

import app.index_it.api.routing.auth.WelcomeActionRoute
import app.index_it.daos.user.UserDao
import app.index_it.models.auth.WelcomeAction
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLDecoder

fun Route.welcomeActionRoute() {
    /**
     * The auth flow starts by determining the welcome action.
     * Depending on the email, a user can either register if there is no account associated with that email
     * or log in if there is.
     * To log in the user must have verified the email address.
     */
    get<WelcomeActionRoute> { request ->
        val email = withContext(Dispatchers.IO) {
            URLDecoder.decode(request.email, "utf-8")
        }

        val userDto = UserDao.getFromEmail(email)

        val action = if (userDto == null)
            WelcomeAction.REGISTER
        else if (!userDto.email_verified)
            WelcomeAction.VERIFY_EMAIL
        else
            WelcomeAction.LOGIN

        call.respond(action.name)
    }
}
