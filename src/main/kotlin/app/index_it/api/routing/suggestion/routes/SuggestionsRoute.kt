package app.index_it.api.routing.suggestion.routes

import app.index_it.api.routing.suggestion.SuggestionRoutes
import app.index_it.daos.suggestions.SuggestionsDao
import app.index_it.models.suggestions.ColorSuggestionsDto
import app.index_it.models.suggestions.ListNameSuggestionsDto
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.suggestionsRoute() {
    get<SuggestionRoutes.ColorsRoute> {
        val colors = SuggestionsDao.getColors()
            ?: ColorSuggestionsDto(
                description = "colors are missing",
                colors = listOf("#000000")
            )

        call.respond(colors)
    }

    get<SuggestionRoutes.ListNamesRoute> {
        val names = SuggestionsDao.getListNames()
            ?: ListNameSuggestionsDto(
                description = "names are missing",
                names = listOf("Vacations")
            )

        call.respond(names)
    }
}
