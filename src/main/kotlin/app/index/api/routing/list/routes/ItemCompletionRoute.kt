package app.index.api.routing.list.routes

import app.index.api.plugins.emitAnalyticsEvent
import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.AnalyticsEventManager
import app.index.core.logic.usecases.ListAuthorizationUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.list.ItemDao
import app.index.data.daos.task.TaskDao
import app.index.data.models.analytics.AnalyticsEventData
import app.index.data.models.lists.ListAuthorizationLevel
import io.ktor.http.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.itemCompletionRoute() {
    val itemDao by inject<ItemDao>()
    val taskDao by inject<TaskDao>()
    val websocketEventManager by inject<WebsocketEventManager>()
    val analyticsEventManager by inject<AnalyticsEventManager>()

    /**
     * completes or un-completes an item and a related task if existing
     *
     * @tag items
     * @operationId item-completion
     * @path list_id the id of the list
     * @path item_id the id of the item
     * @query completed true for completed, false for un-completed
     * @response 200 item completed
     * @response 401 user not authenticated
     * @response 403 missing required list permission: edit
     * @response 404 item or list not found
     */
    put<ListsRoute.ListRoute.ItemsRoute.ItemRoute.CompletionRoute> {
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

        emitAnalyticsEvent(
            analyticsEventManager = analyticsEventManager,
            analyticsEventData = AnalyticsEventData.ItemCompletionEventData(
                user_id = userId,
                list_id = list.id,
                category_id = updatedItem.category_id,
                completed = it.completed
            )
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
