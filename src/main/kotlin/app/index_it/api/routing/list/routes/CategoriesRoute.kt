package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.emitRabbitMqWebsocketEvent
import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.core.extentions.toObjectId
import app.index_it.data.daos.list.CategoryDao
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.websocket.RabbitMqWebsocketEventType
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.categoriesRoute() {
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
                body<List<CategoryDto>>()
            }
        }
    }) {
        val categories = app.index_it.data.daos.list.CategoryDao.getAll(userIdFromSession()!!, it.parent.listId.toObjectId())

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
            body<CategoryDto.CategoryCreateRequestDto> {
                description = "category data"
                required = true
                example("sample-category", CategoryDto.CategoryCreateRequestDto("visited", "#228822"))
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "category created"
                body<CategoryDto>()
            }
        }
    }) {
        val newCategory = call.receive<CategoryDto.CategoryCreateRequestDto>()

        val category = app.index_it.data.daos.list.CategoryDao.create(userIdFromSession()!!, it.parent.listId.toObjectId(), newCategory)

        call.respond(category)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.CATEGORY_CREATED, category)
    }
}
