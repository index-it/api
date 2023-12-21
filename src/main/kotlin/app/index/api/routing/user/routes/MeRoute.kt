package app.index.api.routing.user.routes

import app.index.api.plugins.emitRabbitMqWebsocketEvent
import app.index.api.plugins.userIdFromSession
import app.index.api.routing.user.MeRoute
import app.index.core.exceptions.AuthenticationException
import app.index.data.daos.auth.UserSessionDao
import app.index.data.daos.user.UserDao
import app.index.data.models.auth.UserSessionCookie
import app.index.data.models.websocket.RabbitMqWebsocketEventType
import io.github.smiley4.ktorswaggerui.dsl.resources.delete
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject

fun Route.meRoutes() {
    val userDao by inject<UserDao>()
    val userSessionDao by inject<UserSessionDao>()

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
        val user =
            userDao.get(userIdFromSession()!!)
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

        userDao.delete(userId)
        userSessionDao.deleteAllSessionsOfUser(userId)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.CLOSE_ALL_CLIENT_CONNECTIONS, null)

        call.respond(HttpStatusCode.OK)
    }
}
