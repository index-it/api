package app.index_it.api.routing.user.routes

import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.user.MeRoute
import app.index_it.core.exceptions.AuthenticationException
import app.index_it.daos.UserDao
import app.index_it.daos.UserSessionDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.meRoutes() {
    get<MeRoute> {
        val user = UserDao.get(userIdFromSession()!!)
            ?: throw AuthenticationException()

        // TODO: Do not send full user
        call.respond(user)
    }

    delete<MeRoute> {
        val userId = userIdFromSession()!!

        UserDao.delete(userId)
        UserSessionDao.deleteAllSessionsOfUser(userId)

        // TODO: Delete also lists, daily planners, and everything related to the user
        call.respond(HttpStatusCode.OK)
    }
}
