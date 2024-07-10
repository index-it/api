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

fun Route.categoryRoute() {
    val categoryDao by inject<CategoryDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

    get<ListsRoute.ListRoute.CategoriesRoute.CategoryRoute>({
        tags = listOf("categories")
        operationId = "get-category"
        summary = "gets a single category"
        request {
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("category_id") {
                required = true
                description = "the id of the category"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "category found"
                body<CategoryData>()
            }
            HttpStatusCode.Unauthorized to {
                description = "user not authenticated"
            }
            HttpStatusCode.Forbidden to {
                description = "missing required list permission: view"
            }
            HttpStatusCode.NotFound to {
                description = "category or list not found"
            }
        }
    }) {
        ListAuthorizationUseCase.getListIfAuthorized(
            listId = it.parent.parent.list_id,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.VIEWER
        ) ?: return@get call.respond(HttpStatusCode.NotFound)

        val category = categoryDao.get(it.category_id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(category)
    }

    put<ListsRoute.ListRoute.CategoriesRoute.CategoryRoute>({
        tags = listOf("categories")
        operationId = "update-category"
        summary = "updates a category"
        request {
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("category_id") {
                required = true
                description = "the id of the category"
            }
            body<CategoryData.CategoryUpdateRequestData> {
                required = true
                description = "new data for the category"
                example("sample-category-update", CategoryData.CategoryUpdateRequestData("loved places", "#228822"))
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "category updated"
                body<CategoryData> {
                    description = "the new category data"
                }
            }
            HttpStatusCode.BadRequest to {
                description = "invalid parameters\n${Validations.Category.VALIDATIONS_SUMMARY}"
            }
            HttpStatusCode.Unauthorized to {
                description = "user not authenticated"
            }
            HttpStatusCode.Forbidden to {
                description = "missing required list permission: edit"
            }
            HttpStatusCode.NotFound to {
                description = "category or list not found"
            }
        }
    }) {
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

    delete<ListsRoute.ListRoute.CategoriesRoute.CategoryRoute>({
        tags = listOf("categories")
        operationId = "delete-category"
        summary = "deletes a category"
        description = "deletes a category and *+all** the items and item contents inside it"
        request {
            pathParameter<String>("list_id") {
                required = true
                description = "the id of the list"
            }
            pathParameter<String>("category_id") {
                required = true
                description = "the id of the category"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "category deleted"
            }
            HttpStatusCode.Unauthorized to {
                description = "user not authenticated"
            }
            HttpStatusCode.Forbidden to {
                description = "missing required list permission: edit"
            }
        }
    }) {
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
