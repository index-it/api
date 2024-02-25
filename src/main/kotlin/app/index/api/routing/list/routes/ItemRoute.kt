package app.index.api.routing.list.routes

import app.index.api.plugins.emitWebsocketEvent
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.typedId.newIxId
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.core.logic.websocket.event.content.ItemCreateOrUpdateEventContent
import app.index.core.logic.websocket.event.content.ItemDeleteEventContent
import app.index.data.daos.list.ItemDao
import app.index.data.models.lists.ItemData
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
    val websocketEventManager by inject<WebsocketEventManager>()

    get<ListsRoute.ListRoute.ItemsRoute.ItemRoute>({
        tags = listOf("items")
        operationId = "get-item"
        summary = "gets a single item"
        request {
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("item_id") {
                required = true
                description = "the id of the item"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "item data"
                body<ItemData>()
            }
            HttpStatusCode.NotFound to {
                description = "item or list not found"
            }
        }
    }) {
        val item = itemDao.get(userIdFromSessionOrThrow(), it.item_id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(item)
    }

    put<ListsRoute.ListRoute.ItemsRoute.ItemRoute>({
        tags = listOf("items")
        operationId = "update-item"
        summary = "updates an item"
        request {
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("item_id") {
                required = true
                description = "the id of the item"
            }
            body<ItemData.ItemUpdateRequestData> {
                required = true
                description = "new item data"
                example(
                    "sample-item-update",
                    ItemData.ItemUpdateRequestData(newIxId(), "Milos 🧿", null),
                )
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "item data"
                body<ItemData>()
            }
            HttpStatusCode.NotFound to {
                description = "item or list not found"
            }
        }
    }) {
        val updatedItem = call.receive<ItemData.ItemUpdateRequestData>()

        val newItem = itemDao.update(userIdFromSessionOrThrow(), it.item_id, updatedItem)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(newItem)

        emitWebsocketEvent(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.ITEM_UPDATED,
            content = ItemCreateOrUpdateEventContent(newItem)
        )
    }

    delete<ListsRoute.ListRoute.ItemsRoute.ItemRoute>({
        tags = listOf("items")
        operationId = "delete-item"
        summary = "deletes an item"
        description = "deletes an item and its content"
        request {
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("item_id") {
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
        val deleted = itemDao.delete(userIdFromSessionOrThrow(), it.item_id)

        call.respond(HttpStatusCode.OK)

        if (deleted) {
            emitWebsocketEvent(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.ITEM_DELETED,
                content = ItemDeleteEventContent(it.item_id)
            )
        }
    }
}
