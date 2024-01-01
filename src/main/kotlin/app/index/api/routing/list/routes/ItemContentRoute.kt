package app.index.api.routing.list.routes

import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.data.daos.list.ItemContentDao
import app.index.data.models.lists.ItemContentData
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
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("item_id") {
                required = true
                description = "the id of the item"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "item content"
                body<ItemContentData>()
            }
            HttpStatusCode.NotFound to {
                description = "item not found"
            }
        }
    }) {
        val content = itemContentDao.getOrCreate(userIdFromSessionOrThrow(), it.parent.item_id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(content)
    }

    put<ListsRoute.ListRoute.ItemsRoute.ItemRoute.ContentRoute>({
        tags = listOf("item-contents")
        operationId = "update item content"
        summary = "updates the content of an item"
        request {
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("item_id") {
                required = true
                description = "the id of the item"
            }
            body<ItemContentData.ItemContentCreateOrUpdateRequestData> {
                required = true
                description = "the new item content"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "item content"
                body<ItemContentData>()
            }
            HttpStatusCode.NotFound to {
                description = "item not found"
            }
        }
    }) {
        val updatedItemContent = call.receive<ItemContentData.ItemContentCreateOrUpdateRequestData>()

        val newContent = itemContentDao.update(userIdFromSessionOrThrow(), it.parent.item_id, updatedItemContent)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(newContent)
    }
}
