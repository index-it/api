package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.emitRabbitMqWebsocketEvent
import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.core.extentions.toObjectId
import app.index_it.daos.list.ItemDao
import app.index_it.daos.list.ListDao
import app.index_it.models.lists.ListDto
import app.index_it.models.websocket.RabbitMqWebsocketEventType
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.listRoute() {
    get<ListsRoute.ListRoute> {
        val list = ListDao.get(userIdFromSession()!!, it.listId.toObjectId())
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(list)
    }

    put<ListsRoute.ListRoute> {
        val updatedList = call.receive<ListDto.ListUpdateRequestDto>()
        val list = ListDao.update(userIdFromSession()!!, it.listId.toObjectId(), updatedList)
            ?: return@put call.respond(HttpStatusCode.NotFound)
        call.respond(list)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.LIST_UPDATED, it.listId)
    }

    delete<ListsRoute.ListRoute> {
        ListDao.delete(userIdFromSession()!!, it.listId.toObjectId())
        ItemDao.deleteAllOfList(userIdFromSession()!!, it.listId.toObjectId())
        call.respond(HttpStatusCode.OK)

        // TODO: Decide whether to wrap delete operations in classes (global class maybe? DeleteOperationEvent(id: String) that can be extended too)
        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.LIST_DELETED, it.listId)
    }
}
