package app.index_it.api.routing.list.routes

import app.index_it.api.routing.list.ListsRoute
import app.index_it.daos.templates.ListTemplateDao
import app.index_it.models.lists.ListDto
import app.index_it.models.templates.ListColorsDto
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

    get<ListsRoute.TemplateRoute.ColorsRoute> {
        val colors = ListTemplateDao.getListColors()
            ?: ListColorsDto(
                description = "colors are missing",
                colors = listOf("#000000")
            )

        call.respond(colors)
    }
}
