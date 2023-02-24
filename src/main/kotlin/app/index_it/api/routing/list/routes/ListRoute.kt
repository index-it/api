package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.core.extentions.toDtoId
import app.index_it.daos.list.ItemDao
import app.index_it.daos.list.ListDao
import app.index_it.models.lists.ListDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.get
import io.ktor.server.resources.put
import io.ktor.server.resources.delete
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.listRoute() {
    get<ListsRoute.ListRoute> {
        val list = ListDao.get(userIdFromSession()!!, it.list_id.toDtoId())
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(list)
    }

    put<ListsRoute.ListRoute> {
        val updatedList = call.receive<ListDto.ListUpdateRequestDto>()
        val list = ListDao.update(userIdFromSession()!!, it.list_id.toDtoId(), updatedList)
            ?: return@put call.respond(HttpStatusCode.NotFound)
        call.respond(list)
    }

    delete<ListsRoute.ListRoute> {
        ListDao.delete(userIdFromSession()!!, it.list_id.toDtoId())
        ItemDao.deleteAllOfList(userIdFromSession()!!, it.list_id.toDtoId())
        call.respond(HttpStatusCode.OK)
    }
}
