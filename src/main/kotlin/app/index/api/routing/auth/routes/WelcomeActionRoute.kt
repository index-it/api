package app.index.api.routing.auth.routes

import app.index.api.routing.auth.WelcomeActionRoute
import app.index.core.logic.usecases.UserAuthUseCase
import app.index.data.daos.user.UserDao
import app.index.data.models.auth.WelcomeAction
import app.index.data.models.auth.WelcomeActionResponse
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
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
    get<WelcomeActionRoute>({
        tags = listOf("auth")
        operationId = "welcome-action"
        summary = "retrieve the required first step for a user auth flow"
        description = "this is used to initialize a user auth flow"
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
                description = "welcome action found and sent"
                body<WelcomeActionResponse> {
                    description = "contains the action the user should perform to authenticate"
                    required = true
                    example("registration", WelcomeActionResponse(WelcomeAction.REGISTER)) {
                        summary = "sample register action"
                        description = "this tells the user they need to register in order to authenticate"
                    }
                    example("login", WelcomeActionResponse(WelcomeAction.LOGIN)) {
                        summary = "sample login action"
                        description = "this tells the user they can login to authenticate"
                    }
                }
            }
        }
    }) { request ->
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
