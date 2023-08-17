package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.emitRabbitMqWebsocketEvent
import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.core.extentions.toObjectId
import app.index_it.daos.list.CategoryDao
import app.index_it.models.lists.CategoryDto
import app.index_it.models.websocket.RabbitMqWebsocketEventType
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.categoriesRoute() {
    get<ListsRoute.ListRoute.CategoriesRoute> {
        val categories = CategoryDao.getAll(userIdFromSession()!!, it.parent.listId.toObjectId())

        call.respond(categories)
    }

    post<ListsRoute.ListRoute.CategoriesRoute> {
        val newCategory = call.receive<CategoryDto.CategoryCreateRequestDto>()

        val category = CategoryDao.create(userIdFromSession()!!, it.parent.listId.toObjectId(), newCategory)

        call.respond(category)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.CATEGORY_CREATED, category)
    }
}
