package app.index_it.api.routing.user.routes

import app.index_it.api.plugins.UserSessionId
import app.index_it.api.routing.user.LogoutRoute
import app.index_it.daos.UserSessionDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.logoutRoute() {
    get<LogoutRoute> {
        UserSessionDao.delete(call.sessions.get<UserSessionId>()!!.session_id)
        call.sessions.clear<UserSessionId>()
        call.respond(HttpStatusCode.OK)
    }
}
