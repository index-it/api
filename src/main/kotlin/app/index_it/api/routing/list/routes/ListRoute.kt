package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.emitRabbitMqWebsocketEvent
import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.core.extentions.toObjectId
import app.index_it.data.daos.list.CategoryDao
import app.index_it.data.daos.list.ItemContentDao
import app.index_it.data.daos.list.ItemDao
import app.index_it.data.daos.list.ListDao
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.websocket.RabbitMqWebsocketEventType
import io.github.smiley4.ktorswaggerui.dsl.resources.delete
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.listRoute() {
    get<ListsRoute.ListRoute>({
        tags = listOf("lists")
        operationId = "get-list"
        summary = "gets a single list"
        request {
            pathParameter<String>("listId") {
                required = true
                description = "the id of the list"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the list"
                body<ListDto>()
            }
            HttpStatusCode.NotFound to {
                description = "list not found"
            }
        }
    }) {
        val list = ListDao.get(userIdFromSession()!!, it.listId.toObjectId())
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(list)
    }

    put<ListsRoute.ListRoute>({
        tags = listOf("lists")
        operationId = "update-list"
        summary = "updates a list"
        request {
            pathParameter<String>("listId") {
                required = true
                description = "the id of the list"
            }
            body<ListDto.ListUpdateRequestDto> {
                description = "the new values for the list"
                required = true
                example("sample-update", ListDto.ListUpdateRequestDto("locations", "📍", "#343322"))
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "list updated"
                body<ListDto> {
                    description = "the updated list"
                }
            }
            HttpStatusCode.NotFound to {
                description = "list not found"
            }
        }
    }) {
        val updatedList = call.receive<ListDto.ListUpdateRequestDto>()
        val list = ListDao.update(userIdFromSession()!!, it.listId.toObjectId(), updatedList)
            ?: return@put call.respond(HttpStatusCode.NotFound)
        call.respond(list)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.LIST_UPDATED, it.listId)
    }

    delete<ListsRoute.ListRoute>({
        tags = listOf("lists")
        operationId = "delete-list"
        summary = "deletes a list"
        description = "this deletes the list and **all** of its content, meaning categories, items and item contents of the list will be deleted"
        request {
            pathParameter<String>("listId") {
                required = true
                description = "the id of the list"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "list deleted"
            }
        }
    }) {
        val items = ItemDao.getAll(userIdFromSession()!!, it.listId.toObjectId())
        ItemContentDao.deleteAllOfItems(userIdFromSession()!!, items.map { item -> item.id })

        ItemDao.deleteAllOfList(userIdFromSession()!!, it.listId.toObjectId())

        app.index_it.data.daos.list.CategoryDao.deleteAllOfList(userIdFromSession()!!, it.listId.toObjectId())

        ListDao.delete(userIdFromSession()!!, it.listId.toObjectId())

        call.respond(HttpStatusCode.OK)

        // TODO: Decide whether to wrap delete operations in classes (global class maybe? DeleteOperationEvent(id: String) that can be extended too)
        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.LIST_DELETED, it.listId)
    }
}
