package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.emitRabbitMqWebsocketEvent
import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.core.extentions.toObjectId
import app.index_it.daos.list.ItemDao
import app.index_it.models.lists.ItemDto
import app.index_it.models.websocket.RabbitMqWebsocketEventType
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.itemsRoute() {
    get<ListsRoute.ListRoute.ItemsRoute> {
        val items = when (it.completed) {
            true ->  ItemDao.getAllCompleted(userIdFromSession()!!, it.parent.listId.toObjectId())
            false -> ItemDao.getAllUncompleted(userIdFromSession()!!, it.parent.listId.toObjectId())
            null -> ItemDao.getAll(userIdFromSession()!!, it.parent.listId.toObjectId())
        }

        call.respond(items)
    }

    post<ListsRoute.ListRoute.ItemsRoute> {
        val newItem = call.receive<ItemDto.ItemCreateRequestDto>()

        val item = ItemDao.create(userIdFromSession()!!, it.parent.listId.toObjectId(), newItem)

        call.respond(item)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_CREATED, item)
    }
}
