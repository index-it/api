package app.index.api.routing.list.routes

import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.typedId.newIxId
import app.index.core.logic.usecases.ListAuthorizationUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.list.ItemDao
import app.index.data.daos.task.TaskDao
import app.index.data.models.lists.ItemData
import app.index.data.models.lists.ListAuthorizationLevel
import app.index.data.validation.Validations
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
    val taskDao by inject<TaskDao>()
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
            HttpStatusCode.Unauthorized to {
                description = "user not authenticated"
            }
            HttpStatusCode.Forbidden to {
                description = "missing required list permission: view"
            }
            HttpStatusCode.NotFound to {
                description = "item or list not found"
            }
        }
    }) {
        ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.parent.list_id,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.VIEWER
        ) ?: return@get call.respond(HttpStatusCode.NotFound)

        val item = itemDao.get(it.item_id)
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
            HttpStatusCode.BadRequest to {
                description = "invalid parameters\n${Validations.Item.VALIDATIONS_SUMMARY}"
            }
            HttpStatusCode.Unauthorized to {
                description = "user not authenticated"
            }
            HttpStatusCode.Forbidden to {
                description = "missing required list permission: edit"
            }
            HttpStatusCode.NotFound to {
                description = "item or list not found"
            }
        }
    }) {
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
            HttpStatusCode.Unauthorized to {
                description = "user not authenticated"
            }
            HttpStatusCode.Forbidden to {
                description = "missing required list permission: edit"
            }
        }
    }) {
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
                    users = listOf(unconnectedTask.user_id),
                    includeCurrentSession = unconnectedTask.user_id == userId
                )
            }
        }
    }
}
