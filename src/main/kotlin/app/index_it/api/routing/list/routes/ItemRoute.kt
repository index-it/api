package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.emitRabbitMqWebsocketEvent
import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.core.extentions.toObjectId
import app.index_it.daos.list.ItemDao
import app.index_it.models.lists.ItemDto
import app.index_it.models.websocket.RabbitMqWebsocketEventType
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.itemRoute() {
    get<ListsRoute.ListRoute.ItemsRoute.ItemRoute> {
        val item = ItemDao.get(userIdFromSession()!!, it.parent.parent.listId.toObjectId(), it.itemId.toObjectId())
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(item)
    }

    put<ListsRoute.ListRoute.ItemsRoute.ItemRoute> {
        val updatedItem = call.receive<ItemDto.ItemUpdateRequestDto>()

        val item = ItemDao.update(userIdFromSession()!!, it.parent.parent.listId.toObjectId(), it.itemId.toObjectId(), updatedItem)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(item)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_UPDATED, item)
    }

    delete<ListsRoute.ListRoute.ItemsRoute.ItemRoute> {
        ItemDao.delete(userIdFromSession()!!, it.parent.parent.listId.toObjectId(), it.itemId.toObjectId())
        call.respond(HttpStatusCode.OK)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_DELETED, "${it.parent.parent.listId}:${it.itemId}")
    }
}
