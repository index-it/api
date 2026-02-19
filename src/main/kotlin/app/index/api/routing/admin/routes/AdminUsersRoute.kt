package app.index.api.routing.admin.routes

import app.index.api.routing.admin.AdminRoute
import app.index.core.logic.DatetimeUtils
import app.index.core.logic.PasswordEncoder
import app.index.core.logic.typedId.newIxId
import app.index.data.daos.user.UserDao
import app.index.data.models.user.UserData
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.adminUsersRoute() {
    val userDao by inject<UserDao>()

    /**
     * Creates a new user.
     *
     * Tag: admin
     */
    post<AdminRoute.UsersRoute> {
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
            has_pro = false
        )

        userDao.create(user)

        call.respond(user)
    }
}
