package app.index.api.routing.admin.routes

import app.index.api.plugins.AuthenticationMethods
import app.index.api.routing.admin.AdminRoute
import app.index.core.logic.DatetimeUtils
import app.index.core.logic.PasswordEncoder
import app.index.core.logic.typedId.newIxId
import app.index.data.daos.user.UserDao
import app.index.data.models.user.UserData
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.adminUsersRoute() {
    val userDao by inject<UserDao>()

    post<AdminRoute.UsersRoute>({
        tags = listOf("admin")
        operationId = "create-user"
        summary = "creates a user"
        securitySchemeName = AuthenticationMethods.ADMIN_BEARER_AUTH
        request {
            body<UserData.AdminUserCreateRequestData> {
                description = "the data to create the user (password will be hashed)"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "user created"
                body<UserData> {
                    description = "the created user"
                }
            }
            HttpStatusCode.Conflict to {
                description = "user with the provided email already exists"
            }
        }
    }) {
        val userData = call.receive<UserData.AdminUserCreateRequestData>()

        if (userDao.getFromEmail(userData.email) != null) {
            return@post call.respond(HttpStatusCode.Conflict)
        }

        val user = UserData(
            id = newIxId(),
            email = userData.email,
            passwordHash = PasswordEncoder().encode(userData.password),
            emailVerified = userData.email_verified,
            creationTimestamp = DatetimeUtils.currentMillis(),
            creationSource = userData.creation_source,
        )

        userDao.create(user)

        call.respond(user)
    }
}
