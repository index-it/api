package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.emitRabbitMqWebsocketEvent
import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.daos.list.ListDao
import app.index_it.models.lists.ListDto
import app.index_it.models.websocket.RabbitMqWebsocketEventType
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.listsRoute() {
    get<ListsRoute> {
        call.respond(ListDao.getAll(userIdFromSession()!!))
    }

    post<ListsRoute> {
        val newList = call.receive<ListDto.ListCreateRequestDto>()

        val created = ListDao.create(userIdFromSession()!!, newList)

        call.respond(created)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.LIST_CREATED, created)
    }
}
