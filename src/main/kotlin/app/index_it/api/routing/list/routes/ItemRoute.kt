package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.core.extentions.toDtoId
import app.index_it.daos.list.ItemDao
import app.index_it.models.lists.ItemDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.get
import io.ktor.server.resources.put
import io.ktor.server.resources.delete
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.itemRoute() {
    get<ListsRoute.ListRoute.ItemsRoute.ItemRoute> {
        val item = ItemDao.get(userIdFromSession()!!, it.parent.parent.list_id.toDtoId(), it.item_id.toDtoId())
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(item)
    }

    put<ListsRoute.ListRoute.ItemsRoute.ItemRoute> {
        val updatedItem = call.receive<ItemDto.ItemUpdateRequestDto>()

        val item = ItemDao.update(userIdFromSession()!!, it.parent.parent.list_id.toDtoId(), it.item_id.toDtoId(), updatedItem)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(item)
    }

    delete<ListsRoute.ListRoute.ItemsRoute.ItemRoute> {
        ItemDao.delete(userIdFromSession()!!, it.parent.parent.list_id.toDtoId(), it.item_id.toDtoId())
        call.respond(HttpStatusCode.OK)
    }
}
