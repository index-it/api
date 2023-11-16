package app.index_it.api.routing.user.routes

import app.index_it.api.plugins.emitRabbitMqWebsocketEvent
import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.user.MeRoute
import app.index_it.core.exceptions.AuthenticationException
import app.index_it.data.daos.auth.UserSessionDao
import app.index_it.data.daos.user.UserDao
import app.index_it.data.models.auth.RegistrationCredentials
import app.index_it.data.models.auth.UserSessionCookie
import app.index_it.data.models.websocket.RabbitMqWebsocketEventType
import io.github.smiley4.ktorswaggerui.dsl.resources.delete
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.meRoutes() {
    get<MeRoute>({
        tags = listOf("user")
        operationId = "me"
        summary = "get the logged in user data"
        response {
            HttpStatusCode.OK to {
                description = "user data"
            }
        }
    }) {
        val user = UserDao.get(userIdFromSession()!!)
            ?: throw AuthenticationException()

        call.respond(user.getResponseDto())
    }

    delete<MeRoute>({
        tags = listOf("user")
        operationId = "delete-account"
        summary = "delete the logged in user account"
        description = "this deletes **all** the data of the logged in user from index systems, it's irreversible"
        response {
            HttpStatusCode.OK to {
                description = "user data deleted and session terminated"
            }
        }
    }) {
        val userId = userIdFromSession()!!
        call.sessions.clear<UserSessionCookie>()

        UserDao.delete(userId)
        UserSessionDao.deleteAllSessionsOfUser(userId)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.CLOSE_ALL_CLIENT_CONNECTIONS, null)

        call.respond(HttpStatusCode.OK)
    }
}
