package app.index.api.routing.list.routes

import app.index.api.core.logic.usecases.ListAuthorizationUseCase
import app.index.api.data.daos.list.ItemContentDao
import app.index.api.data.models.lists.ItemContentData
import app.index.api.data.models.lists.ListAuthorizationLevel
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
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
     * Gets the content of an item.
     *
     * Description: If the content doesn't yet exist it gets created.
     *
     * Tag: item-contents
     *
     * Security: session
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
     * Updates the content of an item.
     *
     * Tag: item-contents
     *
     * Security: session
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
