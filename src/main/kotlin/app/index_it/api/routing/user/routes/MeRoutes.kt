package app.index_it.api.routing.user.routes

import app.index_it.api.plugins.emitRabbitMqWebsocketEvent
import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.user.MeRoute
import app.index_it.core.exceptions.AuthenticationException
import app.index_it.daos.user.UserDao
import app.index_it.daos.auth.UserSessionDao
import app.index_it.daos.list.ItemDao
import app.index_it.daos.list.ListDao
import app.index_it.models.websocket.RabbitMqWebsocketEventType
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
        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.CLOSE_ALL_CLIENT_CONNECTIONS, null)
        UserSessionDao.deleteAllSessionsOfUser(userId)

        ListDao.deleteAll(userId)
        ItemDao.deleteAllOfUser(userId)
        // TODO: Delete planner data
        call.respond(HttpStatusCode.OK)
    }
}
