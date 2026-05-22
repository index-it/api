package app.index.api.routing.list.routes

import app.index.api.core.logic.AnalyticsEventManager
import app.index.api.core.logic.usecases.ListAuthorizationUseCase
import app.index.api.core.logic.websocket.WebsocketEventManager
import app.index.api.core.logic.websocket.event.WebsocketEventContent
import app.index.api.core.logic.websocket.event.WebsocketEventType
import app.index.shared.core.data.daos.list.CategoryDao
import app.index.shared.core.data.models.analytics.AnalyticsEventData
import app.index.shared.core.data.models.lists.CategoryData
import app.index.shared.core.data.models.lists.ListAuthorizationLevel
import app.index.api.plugins.emitAnalyticsEvent
import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.categoriesRoute() {
    val categoryDao by inject<CategoryDao>()
    val websocketEventManager by inject<WebsocketEventManager>()
    val analyticsEventManager by inject<AnalyticsEventManager>()

    /**
     * Gets all categories of a list.
     *
     * Tag: categories
     *
     * Security: session
     */
    get<ListsRoute.ListRoute.CategoriesRoute> {
        ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.list_id,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.VIEWER
        ) ?: return@get call.respond(HttpStatusCode.NotFound)

        val categories = categoryDao.getAll(it.parent.list_id)

        call.respond(categories)
    }

    /**
     * Creates a category.
     *
     * Tag: categories
     *
     * Security: session
     */
    post<ListsRoute.ListRoute.CategoriesRoute> {
        val userId = userIdFromSessionOrThrow()
        val list = ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.list_id,
            userId = userId,
            authorizationLevel = ListAuthorizationLevel.EDITOR
        ) ?: return@post call.respond(HttpStatusCode.NotFound)

        val newCategory = call.receive<CategoryData.CategoryCreateRequestData>()

        val category = categoryDao.create(userIdFromSessionOrThrow(), it.parent.list_id, newCategory)

        call.respond(category)

        emitWebsocketEventForUsers(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.CATEGORY_CREATED,
            content = WebsocketEventContent.CategoryCreateOrUpdateEventContent(category),
            users = list.getUsersWithAccess()
        )

        emitAnalyticsEvent(
            analyticsEventManager = analyticsEventManager,
            analyticsEventData = AnalyticsEventData.CategoryCreationEventData(
                user_id = userId,
                list_id = list.id
            )
        )
    }
}
