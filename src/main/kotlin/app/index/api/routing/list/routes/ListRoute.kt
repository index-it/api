package app.index.api.routing.list.routes

import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.usecases.ListAuthorizationUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.list.ListDao
import app.index.data.models.lists.ListAuthorizationLevel
import app.index.data.models.lists.ListData
import app.index.data.validation.Validations
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
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the list"
                body<ListData>()
            }
            HttpStatusCode.Unauthorized to {
                description = "not authorized to perform this action on the list"
            }
            HttpStatusCode.NotFound to {
                description = "list not found"
            }
        }
    }) {
        val list = ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.list_id,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.VIEWER
        )
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(list)
    }

    put<ListsRoute.ListRoute>({
        tags = listOf("lists")
        operationId = "update-list"
        summary = "updates a list"
        request {
            pathParameter<String>("list_id") {
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
            HttpStatusCode.Unauthorized to {
                description = "not authorized to perform this action on the list"
            }
            HttpStatusCode.BadRequest to {
                description = "invalid parameters\n${Validations.List.VALIDATIONS_SUMMARY}"
            }
            HttpStatusCode.NotFound to {
                description = "list not found"
            }
        }
    }) {
        ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.list_id,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.EDITOR
        ) ?: return@put call.respond(HttpStatusCode.NotFound)

        val updatedList = call.receive<ListData.ListUpdateRequestData>()

        val newList = listDao.update(it.list_id, updatedList)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(newList)

        emitWebsocketEventForUsers(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.LIST_UPDATED,
            content = WebsocketEventContent.ListCreateOrUpdateEventContent(newList),
            users = newList.getUsersWithAccess()
        )
    }

    delete<ListsRoute.ListRoute>({
        tags = listOf("lists")
        operationId = "delete-list"
        summary = "deletes a list"
        description = "this deletes the list and **all** of its content, meaning categories, items and item contents of the list will be deleted"
        request {
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "list deleted"
            }
            HttpStatusCode.Unauthorized to {
                description = "not authorized to perform this action on the list"
            }
        }
    }) {
        val list = ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.list_id,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.OWNER
        ) ?: return@delete call.respond(HttpStatusCode.NotFound)

        val deleted = listDao.delete(it.list_id)

        call.respond(HttpStatusCode.OK)

        if (deleted) {
            emitWebsocketEventForUsers(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.LIST_DELETED,
                content = WebsocketEventContent.ListDeleteEventContent(it.list_id),
                users = list.getUsersWithAccess()
            )
        }
    }
}
