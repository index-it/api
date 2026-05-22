package app.index.api.routing.list.routes

import app.index.api.config.ProConfig
import app.index.api.core.logic.AnalyticsEventManager
import app.index.api.core.logic.websocket.WebsocketEventManager
import app.index.api.core.logic.websocket.event.WebsocketEventContent
import app.index.api.core.logic.websocket.event.WebsocketEventType
import app.index.api.data.daos.list.CategoryDao
import app.index.api.data.daos.list.ItemDao
import app.index.api.data.daos.list.ListDao
import app.index.api.data.daos.user.UserDao
import app.index.api.data.models.analytics.AnalyticsEventData
import app.index.api.data.models.lists.ListData
import app.index.api.data.models.lists.ListsSyncResponse
import app.index.api.plugins.emitAnalyticsEvent
import app.index.api.plugins.emitWebsocketEventForCurrentSessionUser
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
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
     * Gets all the lists the user has access to.
     *
     * Tag: lists
     *
     * Security: session
     */
    get<ListsRoute> {
        call.respond(listDao.getListsAccessibleByUser(userIdFromSessionOrThrow()))
    }

    /**
     * Gets all lists, categories, and items.
     *
     * Tag: lists
     *
     * Security: session
     */
    get<ListsRoute.SyncRoute> { req ->
        val userId = userIdFromSessionOrThrow()
        val lists = listDao.getListsAccessibleByUser(userId)
        val categories = lists.flatMap { categoryDao.getAll(it.id) }
        val items = if (req.exclude_items) emptyList() else lists.flatMap {
            when(req.items_completion) {
                true -> itemDao.getAllCompleted(it.id)
                false -> itemDao.getAllUncompleted(it.id)
                null -> itemDao.getAll(it.id)
            }
        }

        call.respond(ListsSyncResponse(lists, categories, items))
    }

    /**
     * Creates a new list.
     *
     * Tag: lists
     *
     * Security: session
     */
    post<ListsRoute> {
        val userId = userIdFromSessionOrThrow()
        val user = userDao.get(userId)
            ?: return@post call.respond(HttpStatusCode.Unauthorized)

        val newList = call.receive<ListData.ListCreateRequestData>()

        val listsCount = listDao.count(userId)
        val canCreateUnlimitedLists = user.has_pro || ProConfig.bypass

        if (listsCount >= 7 && !canCreateUnlimitedLists) {
            return@post call.respond(HttpStatusCode.PaymentRequired)
        }

        if (newList.public && !user.has_pro && !ProConfig.bypass) {
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
