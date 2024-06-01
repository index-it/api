package app.index.api.routing.list.routes

import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.usecases.ListAuthorizationUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.list.ItemDao
import app.index.data.daos.task.TaskDao
import app.index.data.models.lists.ItemData
import app.index.data.models.lists.ListAuthorizationLevel
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
            HttpStatusCode.Unauthorized to {
                description = "not authorized to perform this action on the list"
            }
            HttpStatusCode.NotFound to {
                description = "item or list not found"
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()

        val list = ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.parent.parent.list_id,
            userId = userId,
            authorizationLevel = ListAuthorizationLevel.EDITOR
        ) ?: return@put call.respond(HttpStatusCode.NotFound)

        val updatedItem = itemDao.setCompletion(it.parent.item_id, it.completed)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(updatedItem)

        emitWebsocketEventForUsers(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.ITEM_UPDATED,
            content = WebsocketEventContent.ItemCreateOrUpdateEventContent(updatedItem),
            users = list.getUsersWithAccess()
        )

        // update all connected tasks (multiple users might have a task connected to this item if the list is shared)
        val updatedTasks = taskDao.setCompletionOfAllTasksConnectedToItem(
            it.parent.item_id,
            it.completed
        )

        updatedTasks.forEach { updateTask ->
            emitWebsocketEventForUsers(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.TASK_UPDATED,
                content = WebsocketEventContent.TaskCreateOrUpdateEventContent(updateTask),
                users = listOf(updateTask.user_id),
                includeCurrentSession = updateTask.user_id == userId
            )
        }
    }
}
