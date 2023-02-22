package app.index_it.api.routing.user.routes

import app.index_it.api.plugins.UserSessionCookie
import app.index_it.api.routing.user.LogoutRoute
import app.index_it.core.extentions.toDtoId
import app.index_it.daos.UserSessionDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.logoutRoute() {
    get<LogoutRoute> {
        val session = call.sessions.get<UserSessionCookie>()!!

        UserSessionDao.delete(session.user_id.toDtoId(), session.session_id)

        call.sessions.clear<UserSessionCookie>()
        call.respond(HttpStatusCode.OK)
    }
}
