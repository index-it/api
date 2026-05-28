package app.index.api.routing.list.routes

import app.index.shared.core.config.ProConfig
import app.index.shared.core.logic.usecases.ListAuthorizationUseCase
import app.index.api.core.logic.websocket.WebsocketEventManager
import app.index.api.core.logic.websocket.event.WebsocketEventContent
import app.index.api.core.logic.websocket.event.WebsocketEventType
import app.index.shared.core.data.daos.list.ListDao
import app.index.shared.core.data.daos.user.UserDao
import app.index.shared.core.data.models.lists.ListAuthorizationLevel
import app.index.shared.core.data.models.lists.ListData
import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
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
     * Gets a single list.
     *
     * Tag: lists
     *
     * Security: session
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
     * Updates a list.
     *
     * Tag: lists
     *
     * Security: session
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

            if (!owner.has_pro && !ProConfig.bypass) {
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
     * Deletes a list and all of its content.
     *
     * Tag: lists
     *
     * Security: session
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
