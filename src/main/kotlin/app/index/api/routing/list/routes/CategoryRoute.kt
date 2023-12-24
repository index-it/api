package app.index.api.routing.list.routes

import app.index.api.plugins.emitWebsocketEvent
import app.index.api.plugins.userIdFromSession
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.core.logic.websocket.event.content.impl.CategoryCreateOrUpdateEventContent
import app.index.core.logic.websocket.event.content.impl.CategoryDeleteEventContent
import app.index.data.daos.list.CategoryDao
import app.index.data.models.lists.CategoryData
import io.github.smiley4.ktorswaggerui.dsl.resources.delete
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.categoryRoute() {
    val categoryDao by inject<CategoryDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

    get<ListsRoute.ListRoute.CategoriesRoute.CategoryRoute>({
        tags = listOf("categories")
        operationId = "get-category"
        summary = "gets a single category"
        request {
            pathParameter<String>("listId") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("categoryId") {
                required = true
                description = "the id of the category"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "category found"
                body<CategoryData>()
            }
            HttpStatusCode.NotFound to {
                description = "category or list not found"
            }
        }
    }) {
        val category = categoryDao.get(userIdFromSessionOrThrow(), it.parent.parent.list_id, it.category_id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(category)
    }

    put<ListsRoute.ListRoute.CategoriesRoute.CategoryRoute>({
        tags = listOf("categories")
        operationId = "update-category"
        summary = "updates a category"
        request {
            pathParameter<String>("listId") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("categoryId") {
                required = true
                description = "the id of the category"
            }
            body<CategoryData.CategoryUpdateRequestData> {
                required = true
                description = "new data for the category"
                example("sample-category-update", CategoryData.CategoryUpdateRequestData("loved places", "#228822"))
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "category updated"
                body<CategoryData> {
                    description = "the new category data"
                }
            }
            HttpStatusCode.NotFound to {
                description = "category or list not found"
            }
        }
    }) {
        val updatedCategory = call.receive<CategoryData.CategoryUpdateRequestData>()

        val newCategory = categoryDao.update(userIdFromSessionOrThrow(), it.parent.parent.list_id, it.category_id, updatedCategory)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(newCategory)

        emitWebsocketEvent(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.CATEGORY_UPDATED,
            content = CategoryCreateOrUpdateEventContent(newCategory)
        )
    }

    delete<ListsRoute.ListRoute.CategoriesRoute.CategoryRoute>({
        tags = listOf("categories")
        operationId = "delete-category"
        summary = "deletes a category"
        description = "deletes a category and *+all** the items and item contents inside it"
        request {
            pathParameter<String>("listId") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("categoryId") {
                required = true
                description = "the id of the category"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "category deleted"
            }
        }
    }) {
        val deleted = categoryDao.delete(userIdFromSession()!!, it.parent.parent.list_id, it.category_id)

        call.respond(HttpStatusCode.OK)

        if (deleted) {
            emitWebsocketEvent(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.CATEGORY_DELETED,
                content = CategoryDeleteEventContent(it.category_id)
            )
        }
    }
}
