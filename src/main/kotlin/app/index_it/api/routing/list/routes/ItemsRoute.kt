package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.emitRabbitMqWebsocketEvent
import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.core.extentions.toObjectId
import app.index_it.daos.list.ItemDao
import app.index_it.models.lists.ItemDto
import app.index_it.models.websocket.RabbitMqWebsocketEventType
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId
import org.litote.kmongo.id.toId

fun Route.itemsRoute() {
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
                body<List<ItemDto>>()
            }
        }
    }) {
        val items = when (it.completed) {
            true ->  ItemDao.getAllCompleted(userIdFromSession()!!, it.parent.listId.toObjectId())
            false -> ItemDao.getAllUncompleted(userIdFromSession()!!, it.parent.listId.toObjectId())
            null -> ItemDao.getAll(userIdFromSession()!!, it.parent.listId.toObjectId())
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
            body<ItemDto.ItemCreateRequestDto> {
                required = true
                description = "item data"
                example("sample-item", ItemDto.ItemCreateRequestDto(ObjectId().toId(), "Milos"))
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "item created"
                body<ItemDto>()
            }
        }
    }) {
        val newItem = call.receive<ItemDto.ItemCreateRequestDto>()

        val item = ItemDao.create(userIdFromSession()!!, it.parent.listId.toObjectId(), newItem)

        call.respond(item)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_CREATED, item)
    }
}
