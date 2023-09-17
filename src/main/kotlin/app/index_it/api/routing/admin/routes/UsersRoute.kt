package app.index_it.api.routing.admin.routes

import app.index_it.api.plugins.AuthenticationMethods
import app.index_it.api.routing.admin.AdminRoute
import app.index_it.daos.user.UserDao
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.usersRoute() {
    get<AdminRoute.UsersRoute.VerifyEmailRoute>({
        tags = listOf("admin")
        operationId = "verify-user-email"
        summary = "verifies the email on behalf of a user"
        securitySchemeName = AuthenticationMethods.adminBearerAuth
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
                description = "email verified"
            }
            HttpStatusCode.NotFound to {
                description = "email not found"
            }
        }
    }) {
        UserDao.getFromEmail(it.email)?.let { user ->
            UserDao.verifyEmail(user.id)
        } ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(HttpStatusCode.OK)
    }
}