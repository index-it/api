package app.index.api.routing.list.routes

import app.index.api.plugins.emitAnalyticsEvent
import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.AnalyticsEventManager
import app.index.core.logic.typedId.newIxId
import app.index.core.logic.usecases.ListAuthorizationUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.list.ItemDao
import app.index.data.models.analytics.AnalyticsEventData
import app.index.data.models.lists.ItemData
import app.index.data.models.lists.ListAuthorizationLevel
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.itemsRoute() {
    val itemDao by inject<ItemDao>()
    val websocketEventManager by inject<WebsocketEventManager>()
    val analyticsEventManager by inject<AnalyticsEventManager>()

    get<ListsRoute.ListRoute.ItemsRoute>({
        tags = listOf("items")
        operationId = "get list items"
        summary = "gets all the items of a list"
        request {
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
            queryParameter<Boolean?>("completed") {
                required = false
                description = "completed filter: true means only completed, false only uncompleted, null or missing means all"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "list items"
                body<List<ItemData>>()
            }
            HttpStatusCode.Unauthorized to {
                description = "not authorized to perform this action on the list"
            }
        }
    }) {
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

    post<ListsRoute.ListRoute.ItemsRoute>({
        tags = listOf("items")
        operationId = "create-item"
        summary = "creates a new item in a list"
        request {
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
            body<ItemData.ItemCreateRequestData> {
                required = true
                description = "item data"
                example("sample-item", ItemData.ItemCreateRequestData(newIxId(), "Milos", null))
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "item created"
                body<ItemData>()
            }
            HttpStatusCode.Unauthorized to {
                description = "not authorized to perform this action on the list"
            }
        }
    }) {
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
}
