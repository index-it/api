package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.emitRabbitMqWebsocketEvent
import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.core.extentions.toObjectId
import app.index_it.daos.list.CategoryDao
import app.index_it.daos.list.ItemContentDao
import app.index_it.daos.list.ItemDao
import app.index_it.models.lists.CategoryDto
import app.index_it.models.websocket.RabbitMqWebsocketEventType
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
        val category = CategoryDao.get(userIdFromSession()!!, it.parent.parent.listId.toObjectId(), it.categoryId.toObjectId())
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

        val category = CategoryDao.update(userIdFromSession()!!, it.parent.parent.listId.toObjectId(), it.categoryId.toObjectId(), updatedCategory)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(category)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.CATEGORY_UPDATED, category)
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
        val itemsOfCategory = ItemDao.getAllOfCategory(userIdFromSession()!!, it.parent.parent.listId.toObjectId(), it.categoryId.toObjectId())
        ItemContentDao.deleteAllOfItems(userIdFromSession()!!, itemsOfCategory.map { item -> item.id })

        ItemDao.deleteAllOfCategory(userIdFromSession()!!, it.parent.parent.listId.toObjectId(), it.categoryId.toObjectId())

        val list = CategoryDao.delete(userIdFromSession()!!, it.parent.parent.listId.toObjectId(), it.categoryId.toObjectId())

        call.respond(HttpStatusCode.OK)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.CATEGORY_DELETED, list)
    }
}
