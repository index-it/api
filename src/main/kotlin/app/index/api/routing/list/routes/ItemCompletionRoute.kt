package app.index.api.routing.list.routes

import app.index.api.plugins.emitWebsocketEvent
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.core.logic.websocket.event.content.ItemCreateOrUpdateEventContent
import app.index.data.daos.list.ItemDao
import app.index.data.daos.task.TaskDao
import app.index.data.models.lists.ItemData
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.itemCompletionRoute() {
    val itemDao by inject<ItemDao>()
    val taskDao by inject<TaskDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

    put<ListsRoute.ListRoute.ItemsRoute.ItemRoute.CompletionRoute>({
        tags = listOf("items")
        operationId = "item-completion"
        summary = "completes or un-completes an item"
        description = "this completes or un-completes an item and a related task if existing"
        request {
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("item_id") {
                required = true
                description = "the id of the item"
            }
            queryParameter<Boolean>("completed") {
                required = true
                description = "true for completed, false for un-completed"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "item completed"
                body<ItemData>()
            }
            HttpStatusCode.NotFound to {
                description = "item or list not found"
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()

        val updatedItem = itemDao.setCompletion(userId, it.parent.parent.parent.list_id, it.parent.item_id, it.completed)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        if (updatedItem.task_id != null) {
            taskDao.setCompletion(userId, updatedItem.task_id, it.completed)
        }

        call.respond(updatedItem)

        emitWebsocketEvent(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.ITEM_UPDATED,
            content = ItemCreateOrUpdateEventContent(updatedItem)
        )
    }
}
