package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.core.extentions.toObjectId
import app.index_it.daos.list.ItemContentDao
import app.index_it.models.lists.ItemContentDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.itemContentRoute() {
    get<ListsRoute.ListRoute.ItemsRoute.ItemRoute.ContentRoute> {
        val content = ItemContentDao.getOrCreate(userIdFromSession()!!, it.parent.itemId.toObjectId())
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(content)
    }

    put<ListsRoute.ListRoute.ItemsRoute.ItemRoute.ContentRoute> {
        val updatedItemContent = call.receive<ItemContentDto.ItemContentCreateOrUpdateRequest>()

        val content = ItemContentDao.update(userIdFromSession()!!, it.parent.itemId.toObjectId(), updatedItemContent)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(content)
    }
}