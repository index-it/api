package app.index.api.routing.list.routes

import app.index.api.plugins.emitWebsocketEvent
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.core.logic.websocket.event.content.impl.CategoryCreateOrUpdateEventContent
import app.index.data.daos.list.CategoryDao
import app.index.data.models.lists.CategoryData
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.categoriesRoute() {
    val categoryDao by inject<CategoryDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

    get<ListsRoute.ListRoute.CategoriesRoute>({
        tags = listOf("categories")
        operationId = "get-categories"
        summary = "gets all categories of a list"
        request {
            pathParameter<String>("listId") {
                required = true
                description = "the id of the list"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "categories gotten"
                body<List<CategoryData>>()
            }
        }
    }) {
        val categories = categoryDao.getAll(userIdFromSessionOrThrow(), it.parent.list_id)

        call.respond(categories)
    }

    post<ListsRoute.ListRoute.CategoriesRoute>({
        tags = listOf("categories")
        operationId = "create-category"
        summary = "creates a category"
        request {
            pathParameter<String>("listId") {
                required = true
                description = "the id of the list"
            }
            body<CategoryData.CategoryCreateRequestData> {
                description = "category data"
                required = true
                example("sample-category", CategoryData.CategoryCreateRequestData("visited", "#228822"))
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "category created"
                body<CategoryData>()
            }
        }
    }) {
        val newCategory = call.receive<CategoryData.CategoryCreateRequestData>()

        val category = categoryDao.create(userIdFromSessionOrThrow(), it.parent.list_id, newCategory)

        call.respond(category)

        emitWebsocketEvent(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.CATEGORY_CREATED,
            content = CategoryCreateOrUpdateEventContent(category)
        )
    }
}
