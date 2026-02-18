package app.index.api.routing.list.routes

import app.index.api.plugins.emitAnalyticsEvent
import app.index.api.plugins.emitWebsocketEventForCurrentSessionUser
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.AnalyticsEventManager
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.list.CategoryDao
import app.index.data.daos.list.ItemDao
import app.index.data.daos.list.ListDao
import app.index.data.daos.user.UserDao
import app.index.data.models.analytics.AnalyticsEventData
import app.index.data.models.lists.ListData
import app.index.data.models.lists.ListsSyncResponse
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.listsRoute() {
    val listDao by inject<ListDao>()
    val categoryDao by inject<CategoryDao>()
    val itemDao by inject<ItemDao>()
    val userDao by inject<UserDao>()
    val websocketEventManager by inject<WebsocketEventManager>()
    val analyticsEventManager by inject<AnalyticsEventManager>()

    /**
     * gets all the lists the user has access to
     *
     * @tag lists
     * @operationId get-lists
     * @response 200 user lists
     * @response 401 user not authenticated
     */
    get<ListsRoute> {
        call.respond(listDao.getListsAccessibleByUser(userIdFromSessionOrThrow()))
    }

    /**
     * gets all lists, categories, and items
     *
     * @tag lists
     */
    get<ListsRoute.SyncRoute> {
        val userId = userIdFromSessionOrThrow()
        val lists = listDao.getListsAccessibleByUser(userId)
        val categories = lists.flatMap { categoryDao.getAll(it.id) }
        val items = lists.flatMap { itemDao.getAll(it.id) }

        call.respond(ListsSyncResponse(lists, categories, items))
    }

    /**
     * create a new list
     *
     * @tag lists
     * @operationId create-list
     * @requestBody application/json list create request body
     * @response 200 list created
     * @response 400 invalid parameters
     * @response 401 user not authenticated
     * @response 402 pro required in order to have more than 10 lists and for lists to be public
     */
    post<ListsRoute> {
        val userId = userIdFromSessionOrThrow()
        val user = userDao.get(userId)
            ?: return@post call.respond(HttpStatusCode.Unauthorized)

        val newList = call.receive<ListData.ListCreateRequestData>()

        val listsCount = listDao.count(userId)
        val canCreateUnlimitedLists = user.has_pro

        if (listsCount >= 7 && !canCreateUnlimitedLists) {
            return@post call.respond(HttpStatusCode.PaymentRequired)
        }

        if (newList.public && !user.has_pro) {
            return@post call.respond(HttpStatusCode.PaymentRequired)
        }


        val created = listDao.create(userId, newList)

        call.respond(created)

        emitWebsocketEventForCurrentSessionUser(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.LIST_CREATED,
            content = WebsocketEventContent.ListCreateOrUpdateEventContent(created)
        )

        emitAnalyticsEvent(
            analyticsEventManager = analyticsEventManager,
            analyticsEventData = AnalyticsEventData.ListCreationEventData(
                user_id = userId
            )
        )
    }
}
