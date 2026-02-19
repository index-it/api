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
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.itemRoute() {
    val itemDao by inject<ItemDao>()
    val taskDao by inject<TaskDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

    /**
     * Gets a single item.
     *
     * Tag: items
     *
     * Security: session
     */
    get<ListsRoute.ListRoute.ItemsRoute.ItemRoute> {
        ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.parent.list_id,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.VIEWER
        ) ?: return@get call.respond(HttpStatusCode.NotFound)

        val item = itemDao.get(it.item_id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(item)
    }

    /**
     * Updates an item.
     *
     * Tag: items
     *
     * Security: session
     */
    put<ListsRoute.ListRoute.ItemsRoute.ItemRoute> {
        val list = ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.parent.list_id,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.EDITOR
        ) ?: return@put call.respond(HttpStatusCode.NotFound)

        val updatedItem = call.receive<ItemData.ItemUpdateRequestData>()

        val newItem = itemDao.update(it.item_id, updatedItem)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(newItem)

        emitWebsocketEventForUsers(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.ITEM_UPDATED,
            content = WebsocketEventContent.ItemCreateOrUpdateEventContent(newItem),
            users = list.getUsersWithAccess()
        )
    }

    /**
     * Deletes an item and its content.
     *
     * Tag: items
     *
     * Security: session
     */
    delete<ListsRoute.ListRoute.ItemsRoute.ItemRoute> {
        val userId = userIdFromSessionOrThrow()
        val list = ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.parent.list_id,
            userId = userId,
            authorizationLevel = ListAuthorizationLevel.EDITOR
        ) ?: return@delete call.respond(HttpStatusCode.NotFound)

        val deleted = itemDao.delete(it.item_id)

        call.respond(HttpStatusCode.OK)

        if (deleted) {
            emitWebsocketEventForUsers(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.ITEM_DELETED,
                content = WebsocketEventContent.ItemDeleteEventContent(it.item_id),
                users = list.getUsersWithAccess()
            )

            taskDao.getAllConnectedToItem(it.item_id).forEach { unconnectedTask ->
                emitWebsocketEventForUsers(
                    websocketEventManager = websocketEventManager,
                    type = WebsocketEventType.TASK_UPDATED,
                    content = WebsocketEventContent.TaskCreateOrUpdateEventContent(unconnectedTask),
                    users = setOf(unconnectedTask.user_id),
                    includeCurrentSession = unconnectedTask.user_id == userId
                )
            }
        }
    }
}
