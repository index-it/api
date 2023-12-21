package app.index.api.routing.list.routes

import app.index.api.plugins.emitRabbitMqWebsocketEvent
import app.index.api.plugins.userIdFromSession
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.typedId.newIxId
import app.index.data.daos.list.ItemDao
import app.index.data.models.lists.ItemDto
import app.index.data.models.websocket.RabbitMqWebsocketEventType
import io.github.smiley4.ktorswaggerui.dsl.resources.delete
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.itemRoute() {
    val itemDao by inject<ItemDao>()

    get<ListsRoute.ListRoute.ItemsRoute.ItemRoute>({
        tags = listOf("items")
        operationId = "get-item"
        summary = "gets a single item"
        request {
            pathParameter<String>("listId") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("itemId") {
                required = true
                description = "the id of the item"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "item data"
                body<ItemDto>()
            }
            HttpStatusCode.NotFound to {
                description = "item or list not found"
            }
        }
    }) {
        val item =
            itemDao.get(userIdFromSession()!!, it.parent.parent.listId, it.itemId)
                ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(item)
    }

    put<ListsRoute.ListRoute.ItemsRoute.ItemRoute>({
        tags = listOf("items")
        operationId = "update-item"
        summary = "updates an item"
        request {
            pathParameter<String>("listId") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("itemId") {
                required = true
                description = "the id of the item"
            }
            body<ItemDto.ItemUpdateRequestDto> {
                required = true
                description = "new item data"
                example(
                    "sample-item-update",
                    ItemDto.ItemUpdateRequestDto(newIxId(), "Milos 🧿"),
                )
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "item data"
                body<ItemDto>()
            }
            HttpStatusCode.NotFound to {
                description = "item or list not found"
            }
        }
    }) {
        val updatedItem = call.receive<ItemDto.ItemUpdateRequestDto>()

        val userId = userIdFromSession()!!

        val newItem =
            itemDao.update(userId, it.parent.parent.listId, it.itemId, updatedItem)
                ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(newItem)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_UPDATED, newItem)
    }

    delete<ListsRoute.ListRoute.ItemsRoute.ItemRoute>({
        tags = listOf("items")
        operationId = "delete-item"
        summary = "deletes an item"
        description = "deletes an item and its content"
        request {
            pathParameter<String>("listId") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("itemId") {
                required = true
                description = "the id of the item"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "item deleted"
            }
        }
    }) {
        itemDao.delete(userIdFromSession()!!, it.parent.parent.listId, it.itemId)
        call.respond(HttpStatusCode.OK)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_DELETED, "${it.parent.parent.listId}:${it.itemId}")
    }
}
