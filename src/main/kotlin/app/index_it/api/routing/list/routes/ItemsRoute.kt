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
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.itemsRoute() {
    get<ListsRoute.ListRoute.ItemsRoute> {
        call.respond(ItemDao.getAll(userIdFromSession()!!, it.parent.list_id.toDtoId()))
    }

    post<ListsRoute.ListRoute.ItemsRoute> {
        val newItem = call.receive<ItemDto.ItemCreateRequestDto>()

        val item = ItemDao.create(userIdFromSession()!!, it.parent.list_id.toDtoId(), newItem)

        call.respond(item)
    }
}
