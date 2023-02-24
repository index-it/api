package app.index_it.api.routing.user.routes

import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.user.MeRoute
import app.index_it.core.exceptions.AuthenticationException
import app.index_it.daos.user.UserDao
import app.index_it.daos.auth.UserSessionDao
import app.index_it.daos.list.ItemDao
import app.index_it.daos.list.ListDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.meRoutes() {
    get<MeRoute> {
        val user = UserDao.get(userIdFromSession()!!)
            ?: throw AuthenticationException()

        call.respond(user.getResponseDto())
    }

    delete<MeRoute> {
        val userId = userIdFromSession()!!

        UserDao.delete(userId)
        UserSessionDao.deleteAllSessionsOfUser(userId)

        ListDao.deleteAll(userId)
        ItemDao.deleteAll(userId)
        // TODO: Delete planner data
        call.respond(HttpStatusCode.OK)
    }
}
