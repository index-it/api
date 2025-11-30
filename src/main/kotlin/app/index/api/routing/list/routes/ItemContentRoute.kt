package app.index.api.routing.list.routes

import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.usecases.ListAuthorizationUseCase
import app.index.data.daos.list.ItemContentDao
import app.index.data.models.lists.ItemContentData
import app.index.data.models.lists.ListAuthorizationLevel
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.itemContentRoute() {
    val itemContentDao by inject<ItemContentDao>()

    /**
     * gets the content of an item; if the content doesn't yet exist it gets created
     *
     * @tag item-contents
     * @operationId get item content
     * @path list_id the id of the list
     * @path item_id the id of the item
     * @response 200 item content
     * @response 401 user not authenticated
     * @response 403 missing required list permission: view
     * @response 404 item not found
     */
    get<ListsRoute.ListRoute.ItemsRoute.ItemRoute.ContentRoute> {
        val list = ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.parent.parent.list_id,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.VIEWER
        ) ?: return@get call.respond(HttpStatusCode.NotFound)

        val content = itemContentDao.getOrCreate(list.user_id, it.parent.item_id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(content)
    }

    /**
     * updates the content of an item
     *
     * @tag item-contents
     * @operationId update item content
     * @path list_id the id of the list
     * @path item_id the id of the item
     * @requestBody application/json the new item content
     * @response 200 item content
     * @response 400 invalid parameters
     * @response 401 user not authenticated
     * @response 403 missing required list permission: edit
     * @response 404 item not found
     */
    put<ListsRoute.ListRoute.ItemsRoute.ItemRoute.ContentRoute> {
        ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.parent.parent.list_id,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.EDITOR
        ) ?: return@put call.respond(HttpStatusCode.NotFound)

        val updatedItemContent = call.receive<ItemContentData.ItemContentCreateOrUpdateRequestData>()

        val newContent = itemContentDao.update(it.parent.item_id, updatedItemContent)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(newContent)

    }
}
