package app.index.api.routing.list.routes

import app.index.api.plugins.emitAnalyticsEvent
import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.AnalyticsEventManager
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.usecases.ListAuthorizationUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.list.ItemDao
import app.index.data.daos.task.TaskDao
import app.index.data.models.analytics.AnalyticsEventData
import app.index.data.models.lists.ItemData
import app.index.data.models.lists.ListAuthorizationLevel
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.itemsRoute() {
    val itemDao by inject<ItemDao>()
    val taskDao by inject<TaskDao>()
    val websocketEventManager by inject<WebsocketEventManager>()
    val analyticsEventManager by inject<AnalyticsEventManager>()

    /**
     * Gets all the items of a list.
     *
     * Tag: items
     *
     * Security: session
     */
    get<ListsRoute.ListRoute.ItemsRoute> {
        ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.list_id,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.VIEWER
        ) ?: return@get call.respond(HttpStatusCode.NotFound)

        val items = when (it.completed) {
            true -> itemDao.getAllCompleted(it.parent.list_id)
            false -> itemDao.getAllUncompleted(it.parent.list_id)
            null -> itemDao.getAll(it.parent.list_id)
        }

        call.respond(items)
    }

    /**
     * Creates a new item in a list.
     *
     * Tag: items
     *
     * Security: session
     */
    post<ListsRoute.ListRoute.ItemsRoute> {
        val userId = userIdFromSessionOrThrow()

        val list = ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.list_id,
            userId = userId,
            authorizationLevel = ListAuthorizationLevel.EDITOR
        ) ?: return@post call.respond(HttpStatusCode.NotFound)

        val newItem = call.receive<ItemData.ItemCreateRequestData>()
        val item = itemDao.create(userId, it.parent.list_id, newItem)

        call.respond(item)

        emitWebsocketEventForUsers(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.ITEM_CREATED,
            content = WebsocketEventContent.ItemCreateOrUpdateEventContent(item),
            users = list.getUsersWithAccess()
        )

        emitAnalyticsEvent(
            analyticsEventManager = analyticsEventManager,
            analyticsEventData = AnalyticsEventData.ItemCreationEventData(
                user_id = userId,
                list_id = list.id,
                category_id = newItem.category_id
            )
        )
    }

    /**
     * Move items to a different list.
     *
     * Tag: items
     *
     * Security: session
     */
    put<ListsRoute.ListRoute.ItemsRoute.MoveRoute> {
        val userId = userIdFromSessionOrThrow()
        val originalList = ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.parent.list_id,
            userId = userId,
            authorizationLevel = ListAuthorizationLevel.EDITOR
        ) ?: return@put call.respond(HttpStatusCode.NotFound)

        val updateData = call.receive<ItemData.ItemsMoveRequestData>()

        val newList = if (updateData.list_id != null && updateData.list_id != it.parent.parent.list_id) {
            ListAuthorizationUseCase.getListIfAuthorized(
                listId = updateData.list_id,
                userId = userId,
                authorizationLevel = ListAuthorizationLevel.EDITOR
            ) ?: return@put call.respond(HttpStatusCode.NotFound)
        } else null

        val newItems = itemDao.move(updateData)

        call.respond(newItems)

        if (newList != null) {
            val usersWithAccessToNewList = newList.getUsersWithAccess()
            val usersOnlyInOriginalList = originalList.getUsersWithAccess() - usersWithAccessToNewList

            if (usersWithAccessToNewList.isNotEmpty()) {
                emitWebsocketEventForUsers(
                    websocketEventManager = websocketEventManager,
                    type = WebsocketEventType.ITEMS_UPDATED,
                    content = WebsocketEventContent.ItemsCreateOrUpdateEventContent(newItems),
                    users = usersWithAccessToNewList
                )
            }

            if (usersOnlyInOriginalList.isNotEmpty()) {
                emitWebsocketEventForUsers(
                    websocketEventManager = websocketEventManager,
                    type = WebsocketEventType.ITEMS_DELETED,
                    content = WebsocketEventContent.ItemsDeleteEventContent(newItems.map { it.id }),
                    users = usersOnlyInOriginalList
                )
            }
        } else {
            emitWebsocketEventForUsers(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.ITEMS_UPDATED,
                content = WebsocketEventContent.ItemsCreateOrUpdateEventContent(newItems),
                users = originalList.getUsersWithAccess()
            )
        }

    }

    /**
     * Deletes multiple items.
     *
     * Tag: items
     *
     * Security: session
     */
    delete<ListsRoute.ListRoute.ItemsRoute> {
        val userId = userIdFromSessionOrThrow()
        val list = ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.list_id,
            userId = userId,
            authorizationLevel = ListAuthorizationLevel.EDITOR
        ) ?: return@delete call.respond(HttpStatusCode.NotFound)

        val itemIds = call.receive<List<IxId<ItemData>>>()
        if (itemIds.isEmpty())
            return@delete call.respond(HttpStatusCode.BadRequest, "you didn't provide any item id, provide at least one")

        val deleted = itemDao.delete(itemIds)

        call.respond(HttpStatusCode.OK)

        if (deleted) {
            emitWebsocketEventForUsers(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.ITEMS_DELETED,
                content = WebsocketEventContent.ItemsDeleteEventContent(itemIds),
                users = list.getUsersWithAccess()
            )

            val unconnectedTasks = itemIds.map { itemId -> taskDao.getAllConnectedToItem(itemId) }.flatten()
            emitWebsocketEventForUsers(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.TASKS_UPDATED,
                content = WebsocketEventContent.TasksUpdatedEventContent(unconnectedTasks),
                users = unconnectedTasks.map { task -> task.user_id }.toSet(),
                includeCurrentSession = unconnectedTasks.any { task -> task.user_id == userId }
            )
        }
    }
}
