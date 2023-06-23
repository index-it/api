package app.index_it.api.routing.list.routes

import app.index_it.api.routing.list.ListsRoute
import app.index_it.daos.templates.ListTemplateDao
import app.index_it.models.lists.ListDto
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.resources.*
import io.ktor.server.response.*

fun Route.templateRoute() {
    get<ListsRoute.TemplateRoute> {
        val name = ListTemplateDao.getRandomListName()
        val color = ListTemplateDao.getRandomListColor()

        call.respond(ListDto.ListTemplateResponseDto(name, color))
    }
}
