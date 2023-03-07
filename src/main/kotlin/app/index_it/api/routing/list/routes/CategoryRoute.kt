package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.emitRabbitMqWebsocketEvent
import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.core.extentions.toDtoId
import app.index_it.daos.list.CategoryDao
import app.index_it.daos.list.ItemDao
import app.index_it.models.lists.CategoryDto
import app.index_it.models.websocket.RabbitMqWebsocketEventType
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.get
import io.ktor.server.resources.put
import io.ktor.server.resources.delete
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.categoryRoute() {
    get<ListsRoute.ListRoute.CategoriesRoute.CategoryRoute> {
        val category = CategoryDao.get(userIdFromSession()!!, it.parent.parent.list_id.toDtoId(), it.category_id.toDtoId())
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(category)
    }

    put<ListsRoute.ListRoute.CategoriesRoute.CategoryRoute> {
        val updatedCategory = call.receive<CategoryDto.CategoryUpdateRequestDto>()

        val list = CategoryDao.update(userIdFromSession()!!, it.parent.parent.list_id.toDtoId(), it.category_id.toDtoId(), updatedCategory)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(list)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.CATEGORY_UPDATED, list)
    }

    delete<ListsRoute.ListRoute.CategoriesRoute.CategoryRoute> {
        val list = CategoryDao.delete(userIdFromSession()!!, it.parent.parent.list_id.toDtoId(), it.category_id.toDtoId())
        ItemDao.deleteAllOfCategory(userIdFromSession()!!, it.parent.parent.list_id.toDtoId(), it.category_id.toDtoId())
        call.respond(HttpStatusCode.OK)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.CATEGORY_DELETED, list)
    }
}
