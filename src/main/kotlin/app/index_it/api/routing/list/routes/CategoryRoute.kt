package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.emitRabbitMqWebsocketEvent
import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.data.daos.list.CategoryDao
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.websocket.RabbitMqWebsocketEventType
import io.github.smiley4.ktorswaggerui.dsl.resources.delete
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.categoryRoute() {
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
                body<CategoryDto>()
            }
            HttpStatusCode.NotFound to {
                description = "category or list not found"
            }
        }
    }) {
        val category = CategoryDao.get(userIdFromSession()!!, it.parent.parent.listId, it.categoryId)
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
            body<CategoryDto.CategoryUpdateRequestDto> {
                required = true
                description = "new data for the category"
                example("sample-category-update", CategoryDto.CategoryUpdateRequestDto("loved places", "#228822"))
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "category updated"
                body<CategoryDto> {
                    description = "the new category data"
                }
            }
            HttpStatusCode.NotFound to {
                description = "category or list not found"
            }
        }
    }) {
        val updatedCategory = call.receive<CategoryDto.CategoryUpdateRequestDto>()
        val userId = userIdFromSession()!!

        val newCategory = CategoryDao.update(userId, it.parent.parent.listId, it.categoryId, updatedCategory)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(newCategory)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.CATEGORY_UPDATED, newCategory)
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
        val list = CategoryDao.delete(userIdFromSession()!!, it.parent.parent.listId, it.categoryId)

        call.respond(HttpStatusCode.OK)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.CATEGORY_DELETED, list)
    }
}
