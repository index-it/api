package app.index.api.routing.list.routes

import app.index.api.core.logic.AnalyticsEventManager
import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.core.logic.usecases.ListAuthorizationUseCase
import app.index.api.core.logic.websocket.WebsocketEventManager
import app.index.api.core.logic.websocket.event.WebsocketEventContent
import app.index.api.core.logic.websocket.event.WebsocketEventType
import app.index.api.data.daos.list.ItemDao
import app.index.api.data.daos.task.TaskDao
import app.index.api.data.models.analytics.AnalyticsEventData
import app.index.api.data.models.lists.ItemData
import app.index.api.data.models.lists.ListAuthorizationLevel
import app.index.api.plugins.emitAnalyticsEvent
import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import io.ktor.http.*
import io.ktor.server.request.*
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
     * Completes or un-completes multiple items and related tasks.
     *
     * Tag: items
     *
     * Security: session
     */
    put<ListsRoute.ListRoute.ItemsRoute.CompletionRoute> {
        val userId = userIdFromSessionOrThrow()

        val list = ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.parent.list_id,
            userId = userId,
            authorizationLevel = ListAuthorizationLevel.EDITOR
        ) ?: return@put call.respond(HttpStatusCode.NotFound)

        val itemIds = call.receive<List<IxId<ItemData>>>()
        val updatedItems = itemDao.setCompletion(itemIds, it.completed)

        call.respond(updatedItems)

        emitWebsocketEventForUsers(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.ITEMS_UPDATED,
            content = WebsocketEventContent.ItemsCreateOrUpdateEventContent(updatedItems),
            users = list.getUsersWithAccess()
        )

        updatedItems.forEach { updatedItem ->
            emitAnalyticsEvent(
                analyticsEventManager = analyticsEventManager,
                analyticsEventData = AnalyticsEventData.ItemCompletionEventData(
                    user_id = userId,
                    list_id = list.id,
                    category_id = updatedItem.category_id,
                    completed = it.completed
                )
            )
        }

        // update all connected tasks (multiple users might have a task connected to this item if the list is shared)
        val updatedTasks = itemIds.map { itemId -> taskDao.setCompletionOfAllTasksConnectedToItem(itemId, it.completed) }.flatten()

        emitWebsocketEventForUsers(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.TASKS_UPDATED,
            content = WebsocketEventContent.TasksUpdatedEventContent(updatedTasks),
            users = updatedTasks.map { task -> task.user_id }.toSet(),
            includeCurrentSession = updatedTasks.any { task -> task.user_id == userId }
        )
    }


    /**
     * Completes or un-completes an item and a related task if existing.
     *
     * Tag: items
     *
     * Security: session
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
                users = setOf(updateTask.user_id),
                includeCurrentSession = updateTask.user_id == userId
            )
        }
    }
}
