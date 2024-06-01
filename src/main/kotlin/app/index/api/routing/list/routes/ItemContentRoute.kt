package app.index.api.routing.list.routes

import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.usecases.ListAuthorizationUseCase
import app.index.data.daos.list.ItemContentDao
import app.index.data.models.lists.ItemContentData
import app.index.data.models.lists.ListAuthorizationLevel
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.itemContentRoute() {
    val itemContentDao by inject<ItemContentDao>()

    get<ListsRoute.ListRoute.ItemsRoute.ItemRoute.ContentRoute>({
        tags = listOf("item-contents")
        operationId = "get item content"
        summary = "gets the content of an item"
        description = "get the content of an item, if the content doesn't yet exist it gets created"
        request {
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("item_id") {
                required = true
                description = "the id of the item"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "item content"
                body<ItemContentData>()
            }
            HttpStatusCode.Unauthorized to {
                description = "not authorized to perform this action on the list"
            }
            HttpStatusCode.NotFound to {
                description = "item not found"
            }
        }
    }) {
        val list = ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.parent.parent.list_id,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.VIEWER
        ) ?: return@get call.respond(HttpStatusCode.NotFound)

        val content = itemContentDao.getOrCreate(list.user_id, it.parent.item_id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(content)
    }

    put<ListsRoute.ListRoute.ItemsRoute.ItemRoute.ContentRoute>({
        tags = listOf("item-contents")
        operationId = "update item content"
        summary = "updates the content of an item"
        request {
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("item_id") {
                required = true
                description = "the id of the item"
            }
            body<ItemContentData.ItemContentCreateOrUpdateRequestData> {
                required = true
                description = "the new item content"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "item content"
                body<ItemContentData>()
            }
            HttpStatusCode.Unauthorized to {
                description = "not authorized to perform this action on the list"
            }
            HttpStatusCode.NotFound to {
                description = "item not found"
            }
        }
    }) {
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
