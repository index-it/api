package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.emitRabbitMqWebsocketEvent
import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.data.daos.list.ListDao
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.websocket.RabbitMqWebsocketEventType
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.listsRoute() {
    val listDao by inject<ListDao>()

    get<ListsRoute>({
        tags = listOf("lists")
        operationId = "get-lists"
        summary = "gets all the lists the user has access to"
        response {
            HttpStatusCode.OK to {
                description = "user lists"
                body<List<ListDto>>()
            }
        }
    }) {
        call.respond(listDao.getAll(userIdFromSession()!!))
    }

    post<ListsRoute>({
        tags = listOf("lists")
        operationId = "create-list"
        summary = "create a new list"
        request {
            body<ListDto.ListCreateRequestDto> {
                required = true
                example("sample-list", ListDto.ListCreateRequestDto("places", "🏝️", "#343322"))
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "list created"
                body<ListDto> {
                    description = "the created list"
                }
            }
        }
    }) {
        val newList = call.receive<ListDto.ListCreateRequestDto>()

        val created = listDao.create(userIdFromSession()!!, newList)

        call.respond(created)

        emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.LIST_CREATED, created)
    }
}
