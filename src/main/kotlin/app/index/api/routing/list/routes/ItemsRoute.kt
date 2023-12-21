package app.index.api.routing.list.routes

import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.typedId.newIxId
import app.index.data.daos.list.ItemDao
import app.index.data.models.lists.ItemData
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.itemsRoute() {
    val itemDao by inject<ItemDao>()

    get<ListsRoute.ListRoute.ItemsRoute>({
        tags = listOf("items")
        operationId = "get list items"
        summary = "gets all the items of a list"
        request {
            pathParameter<String>("listId") {
                required = true
                description = "the id of the list"
            }
            queryParameter<Boolean?>("completed") {
                required = false
                description = "completed filter: true means only completed, false only uncompleted, null or missing means all"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "list items"
                body<List<ItemData>>()
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()

        val items = when (it.completed) {
            true -> itemDao.getAllCompleted(userId, it.parent.listId)
            false -> itemDao.getAllUncompleted(userId, it.parent.listId)
            null -> itemDao.getAll(userId, it.parent.listId)
        }

        call.respond(items)
    }

    post<ListsRoute.ListRoute.ItemsRoute>({
        tags = listOf("items")
        operationId = "create-item"
        summary = "creates a new item in a list"
        request {
            pathParameter<String>("listId") {
                required = true
                description = "the id of the list"
            }
            body<ItemData.ItemCreateRequestData> {
                required = true
                description = "item data"
                example("sample-item", ItemData.ItemCreateRequestData(newIxId(), "Milos"))
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "item created"
                body<ItemData>()
            }
        }
    }) {
        val newItem = call.receive<ItemData.ItemCreateRequestData>()

        val item = itemDao.create(userIdFromSessionOrThrow(), it.parent.listId, newItem)

        call.respond(item)
    }
}
