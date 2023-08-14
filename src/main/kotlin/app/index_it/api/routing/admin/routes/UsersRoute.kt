package app.index_it.api.routing.admin.routes

import app.index_it.api.routing.admin.AdminRoute
import app.index_it.daos.user.UserDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.usersRoute() {
    get<AdminRoute.UsersRoute.VerifyEmailRoute> {
        UserDao.getFromEmail(it.email)?.let { user ->
            UserDao.verifyEmail(user.id)
        }

        call.respond(HttpStatusCode.OK)
    }
}