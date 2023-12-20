package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.data.daos.list.ItemContentDao
import app.index_it.data.models.lists.ItemContentDto
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.itemContentRoute() {
    val itemContentDao by inject<ItemContentDao>()

    get<ListsRoute.ListRoute.ItemsRoute.ItemRoute.ContentRoute>({
        tags = listOf("item-contents")
        operationId = "get item content"
        summary = "gets the content of an item"
        description = "get the content of an item, if the content doesn't yet exist it gets created"
        request {
            pathParameter<String>("listId") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("itemId") {
                required = true
                description = "the id of the item"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "item content"
                body<ItemContentDto>()
            }
            HttpStatusCode.NotFound to {
                description = "item not found"
            }
        }
    }) {
        val content = itemContentDao.getOrCreate(userIdFromSession()!!, it.parent.itemId)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(content)
    }

    put<ListsRoute.ListRoute.ItemsRoute.ItemRoute.ContentRoute>({
        tags = listOf("item-contents")
        operationId = "update item content"
        summary = "updates the content of an item"
        request {
            pathParameter<String>("listId") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("itemId") {
                required = true
                description = "the id of the item"
            }
            body<ItemContentDto.ItemContentCreateOrUpdateRequest> {
                required = true
                description = "the new item content"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "item content"
                body<ItemContentDto>()
            }
            HttpStatusCode.NotFound to {
                description = "item not found"
            }
        }
    }) {
        val updatedItemContent = call.receive<ItemContentDto.ItemContentCreateOrUpdateRequest>()
        val userId = userIdFromSession()!!

        val newContent = itemContentDao.update(userId, it.parent.itemId, updatedItemContent)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(newContent)
    }
}