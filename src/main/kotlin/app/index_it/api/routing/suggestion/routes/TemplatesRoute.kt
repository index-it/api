package app.index_it.api.routing.suggestion.routes

import app.index_it.api.routing.suggestion.SuggestionRoutes
import app.index_it.daos.suggestions.SuggestionsDao
import app.index_it.models.lists.ListDto
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.templatesRoute() {
    get<SuggestionRoutes.TemplateRoute.ListRoute> {
        val name = SuggestionsDao.getRandomListName()
        val color = SuggestionsDao.getRandomColor()

        call.respond(ListDto.ListTemplateResponseDto(name, color))
    }
}
