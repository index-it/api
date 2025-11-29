package app.index.api.routing.list.routes

import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.usecases.ListAuthorizationUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.list.ListDao
import app.index.data.daos.user.UserDao
import app.index.data.models.lists.ListAuthorizationLevel
import app.index.data.models.lists.ListData
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.listRoute() {
    val listDao by inject<ListDao>()
    val userDao by inject<UserDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

    /**
     * gets a single list
     *
     * @tag lists
     * @operationId get-list
     * @path list_id the id of the list
     * @response 200 the list
     * @response 401 user not authenticated
     * @response 403 missing required list permission: view
     * @response 404 list not found
     */
    get<ListsRoute.ListRoute> {
        val list = ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.list_id,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.VIEWER
        )
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(list)
    }

    /**
     * updates a list
     *
     * @tag lists
     * @operationId update-list
     * @path list_id the id of the list
     * @requestBody application/json the new values for the list
     * @response 200 list updated
     * @response 400 invalid parameters
     * @response 401 user not authenticated
     * @response 403 missing required list permission: edit
     * @response 402 pro required for lists to be public
     * @response 404 list not found
     */
    put<ListsRoute.ListRoute> {
        val list = ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.list_id,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.EDITOR
        ) ?: return@put call.respond(HttpStatusCode.NotFound)

        val updatedList = call.receive<ListData.ListUpdateRequestData>()

        if (updatedList.public) {
            val owner = userDao.get(list.user_id)
                ?: return@put call.respond(HttpStatusCode.NotFound)

            if (!owner.has_pro) {
                return@put call.respond(HttpStatusCode.PaymentRequired)
            }
        }

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

    /**
     * deletes a list and all of its content (categories, items and item contents)
     *
     * @tag lists
     * @operationId delete-list
     * @path list_id the id of the list
     * @response 200 list deleted
     * @response 401 user not authenticated
     * @response 403 missing required list permission: owner
     */
    delete<ListsRoute.ListRoute> {
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
