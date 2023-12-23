package app.index.api.routing.list.routes

import app.index.api.plugins.emitWebsocketEvent
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.core.logic.websocket.event.content.impl.ListCreateOrUpdateEventContent
import app.index.core.logic.websocket.event.content.impl.ListDeleteEventContent
import app.index.data.daos.list.ListDao
import app.index.data.models.lists.ListData
import io.github.smiley4.ktorswaggerui.dsl.resources.delete
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.listRoute() {
    val listDao by inject<ListDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

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
                body<ListData>()
            }
            HttpStatusCode.NotFound to {
                description = "list not found"
            }
        }
    }) {
        val list = listDao.get(userIdFromSessionOrThrow(), it.listId)
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
            body<ListData.ListUpdateRequestData> {
                description = "the new values for the list"
                required = true
                example("sample-update", ListData.ListUpdateRequestData("locations", "📍", "#343322"))
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "list updated"
                body<ListData> {
                    description = "the updated list"
                }
            }
            HttpStatusCode.NotFound to {
                description = "list not found"
            }
        }
    }) {
        val updatedList = call.receive<ListData.ListUpdateRequestData>()

        val newList = listDao.update(userIdFromSessionOrThrow(), it.listId, updatedList)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(newList)

        emitWebsocketEvent(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.LIST_UPDATED,
            content = ListCreateOrUpdateEventContent(newList)
        )
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
        val deleted = listDao.delete(userIdFromSessionOrThrow(), it.listId)

        call.respond(HttpStatusCode.OK)

        if (deleted) {
            emitWebsocketEvent(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.LIST_DELETED,
                content = ListDeleteEventContent(it.listId)
            )
        }
    }
}
