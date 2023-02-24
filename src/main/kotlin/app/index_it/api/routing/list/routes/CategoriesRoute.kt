package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.core.extentions.toDtoId
import app.index_it.daos.list.CategoryDao
import app.index_it.models.lists.CategoryDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.categoriesRoute() {
    get<ListsRoute.ListRoute.CategoriesRoute> {
        val categories = CategoryDao.getAll(userIdFromSession()!!, it.parent.list_id.toDtoId())
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(categories)
    }

    post<ListsRoute.ListRoute.CategoriesRoute> {
        val newCategory = call.receive<CategoryDto.CategoryCreateRequestDto>()

        val cateogory = CategoryDao.create(userIdFromSession()!!, it.parent.list_id.toDtoId(), newCategory)
            ?: return@post call.respond(HttpStatusCode.NotFound)

        call.respond(cateogory)
    }
}
