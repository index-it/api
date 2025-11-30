package app.index.api.routing.list.routes

import app.index.api.plugins.emitWebsocketEventForUsers
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.usecases.ListAuthorizationUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.list.CategoryDao
import app.index.data.models.lists.CategoryData
import app.index.data.models.lists.ListAuthorizationLevel
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.categoryRoute() {
    val categoryDao by inject<CategoryDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

    /**
     * gets a single category
     *
     * @tag categories
     * @operationId get-category
     * @path list_id the id of the list
     * @path category_id the id of the category
     * @response 200 category found
     * @response 401 user not authenticated
     * @response 403 missing required list permission: view
     * @response 404 category or list not found
     */
    get<ListsRoute.ListRoute.CategoriesRoute.CategoryRoute> {
        ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.parent.list_id,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.VIEWER
        ) ?: return@get call.respond(HttpStatusCode.NotFound)

        val category = categoryDao.get(it.category_id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(category)
    }

    /**
     * updates a category
     *
     * @tag categories
     * @operationId update-category
     * @path list_id the id of the list
     * @path category_id the id of the category
     * @requestBody application/json new data for the category
     * @response 200 category updated
     * @response 400 invalid parameters
     * @response 401 user not authenticated
     * @response 403 missing required list permission: edit
     * @response 404 category or list not found
     */
    put<ListsRoute.ListRoute.CategoriesRoute.CategoryRoute> {
        val list = ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.parent.list_id,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.EDITOR
        ) ?: return@put call.respond(HttpStatusCode.NotFound)

        val updatedCategory = call.receive<CategoryData.CategoryUpdateRequestData>()

        val newCategory = categoryDao.update(it.category_id, updatedCategory)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(newCategory)

        emitWebsocketEventForUsers(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.CATEGORY_UPDATED,
            content = WebsocketEventContent.CategoryCreateOrUpdateEventContent(newCategory),
            users = list.getUsersWithAccess()
        )
    }

    /**
     * deletes a category and all the items and item contents inside it
     *
     * @tag categories
     * @operationId delete-category
     * @path list_id the id of the list
     * @path category_id the id of the category
     * @response 200 category deleted
     * @response 401 user not authenticated
     * @response 403 missing required list permission: edit
     */
    delete<ListsRoute.ListRoute.CategoriesRoute.CategoryRoute> {
        val list = ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.parent.list_id,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.EDITOR
        ) ?: return@delete call.respond(HttpStatusCode.NotFound)

        val deleted = categoryDao.delete(it.category_id)

        call.respond(HttpStatusCode.OK)

        if (deleted) {
            emitWebsocketEventForUsers(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.CATEGORY_DELETED,
                content = WebsocketEventContent.CategoryDeleteEventContent(it.category_id),
                users = list.getUsersWithAccess()
            )
        }
    }
}
