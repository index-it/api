package app.index.api.routing.list.routes

import app.index.api.plugins.emitRabbitMqWebsocketEvent
import app.index.api.plugins.userIdFromSession
import app.index.api.routing.list.ListsRoute
import app.index.data.daos.list.CategoryDao
import app.index.data.models.lists.CategoryDto
import app.index.data.models.websocket.RabbitMqWebsocketEventType
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
        val categories = categoryDao.getAll(userIdFromSession()!!, it.parent.listId)

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

        val category = categoryDao.create(userIdFromSession()!!, it.parent.listId, newCategory)

        call.respond(category)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.CATEGORY_CREATED, category)
    }
}
