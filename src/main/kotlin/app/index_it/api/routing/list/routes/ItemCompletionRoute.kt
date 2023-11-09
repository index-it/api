package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.core.extentions.toObjectId
import app.index_it.data.daos.list.ItemDao
import app.index_it.data.daos.task.TaskDao
import app.index_it.data.models.lists.ItemDto
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.itemCompletionRoute() {
    put<ListsRoute.ListRoute.ItemsRoute.ItemRoute.CompletionRoute>({
        tags = listOf("items")
        operationId = "item-completion"
        summary = "completes or un-completes an item"
        description = "this completes or un-completes an item and a related task if existing"
        request {
            pathParameter<String>("listId") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("itemId") {
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
                body<ItemDto>()
            }
            HttpStatusCode.NotFound to {
                description = "item or list not found"
            }
        }
    }) {
        val userId = userIdFromSession()!!
        // TODO: Maybe change string args to Id<> ones since they support serialization?
        val item = ItemDao.setCompletion(userId, it.parent.parent.parent.listId.toObjectId(), it.parent.itemId.toObjectId(), it.completed)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        if (item.taskId != null) {
            TaskDao.setCompletion(userId, item.taskId, it.completed)
        }

        call.respond(item)
    }
}