package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.emitRabbitMqWebsocketEvent
import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.core.extentions.toObjectId
import app.index_it.daos.list.ItemContentDao
import app.index_it.daos.list.ItemDao
import app.index_it.models.lists.ItemDto
import app.index_it.models.websocket.RabbitMqWebsocketEventType
import io.github.smiley4.ktorswaggerui.dsl.resources.delete
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId
import org.litote.kmongo.id.toId

fun Route.itemRoute() {
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
        val item = ItemDao.get(userIdFromSession()!!, it.parent.parent.listId.toObjectId(), it.itemId.toObjectId())
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
                    ItemDto.ItemUpdateRequestDto(ObjectId().toId(), "Milos 🧿")
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

        val item = ItemDao.update(userIdFromSession()!!, it.parent.parent.listId.toObjectId(), it.itemId.toObjectId(), updatedItem)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(item)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_UPDATED, item)
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
        ItemDao.delete(userIdFromSession()!!, it.parent.parent.listId.toObjectId(), it.itemId.toObjectId())
        ItemContentDao.delete(userIdFromSession()!!, it.itemId.toObjectId())
        call.respond(HttpStatusCode.OK)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_DELETED, "${it.parent.parent.listId}:${it.itemId}")
    }
}
