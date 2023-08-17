package app.index_it.api.routing.suggestion.routes

import app.index_it.api.routing.suggestion.SuggestionRoutes
import app.index_it.daos.suggestions.SuggestionsDao
import app.index_it.models.lists.CategoryDto
import app.index_it.models.lists.ItemDto
import app.index_it.models.lists.ListDto
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.templatesRoute() {
    get<SuggestionRoutes.TemplateRoute.ListRoute> {
        val name = SuggestionsDao.getRandomNameSuggestion(SuggestionsDao.getListNames())
        val color = SuggestionsDao.getRandomColor()

        call.respond(ListDto.ListTemplateResponseDto(name, color))
    }

    get<SuggestionRoutes.TemplateRoute.CategoryRoute> {
        val name = SuggestionsDao.getRandomNameSuggestion(SuggestionsDao.getCategoryNames())
        val color = SuggestionsDao.getRandomColor()

        call.respond(CategoryDto.CategoryTemplateResponseDto(name, color))
    }

    get<SuggestionRoutes.TemplateRoute.ItemRoute> {
        val name = SuggestionsDao.getRandomNameSuggestion(SuggestionsDao.getItemNames())

        call.respond(ItemDto.ItemTemplateResponseDto(name))
    }
}
