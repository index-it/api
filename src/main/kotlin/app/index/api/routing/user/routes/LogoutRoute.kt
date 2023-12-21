package app.index.api.routing.user.routes

import app.index.api.routing.user.LogoutRoute
import app.index.data.daos.auth.UserSessionDao
import app.index.data.models.auth.UserSessionCookie
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject

fun Route.logoutRoutes() {
    val userSessionDao by inject<UserSessionDao>()

    get<LogoutRoute>({
        tags = listOf("auth")
        operationId = "logout"
        summary = "terminates the auth session"
        response {
            HttpStatusCode.OK to {
                description = "session terminated"
            }
        }
    }) {
        val session = call.sessions.get<UserSessionCookie>()!!

        userSessionDao.delete(session.userId, session.sessionId)

        // WebsocketConnectionsManager.closeConnection(session.sessionId)
        call.sessions.clear<UserSessionCookie>()
        call.respond(HttpStatusCode.OK)
    }
}
