package app.index_it.api.routing.suggestion.routes

import app.index_it.api.routing.suggestion.SuggestionRoutes
import app.index_it.core.logic.typedId.newIxIntId
import app.index_it.data.daos.suggestions.SuggestionsDao
import app.index_it.data.models.suggestions.ColorSuggestionsDto
import app.index_it.data.models.suggestions.NameSuggestionsDto
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.suggestionsRoute() {
    get<SuggestionRoutes.ColorsRoute>({
        tags = listOf("suggestions")
        operationId = "colors-suggestion"
        summary = "gets color suggestions"
        response {
            HttpStatusCode.OK to {
                description = "suggested colors"
                body<ColorSuggestionsDto>()
            }
        }
    }) {
        val colors = SuggestionsDao.getColors()
            ?: ColorSuggestionsDto(
                id = newIxIntId(),
                description = "colors are missing",
                colors = listOf("#000000", "#FFFFFF")
            )

        call.respond(colors)
    }

    get<SuggestionRoutes.ListNamesRoute>({
        tags = listOf("suggestions")
        operationId = "list-names-suggestion"
        summary = "gets list name suggestions"
        response {
            HttpStatusCode.OK to {
                description = "suggested list names"
                body<NameSuggestionsDto>()
            }
        }
    }) {
        val names = SuggestionsDao.getListNames()
            ?: NameSuggestionsDto(
                id = newIxIntId(),
                description = "names are missing",
                names = listOf("Vacations")
            )

        call.respond(names)
    }

    get<SuggestionRoutes.CategoryNamesRoute>({
        tags = listOf("suggestions")
        operationId = "category-names-suggestion"
        summary = "gets category name suggestions"
        response {
            HttpStatusCode.OK to {
                description = "suggested category names"
                body<NameSuggestionsDto>()
            }
        }
    }) {
        val names = SuggestionsDao.getCategoryNames()
            ?: NameSuggestionsDto(
                id = newIxIntId(),
                description = "names are missing",
                names = listOf("Planned", "In progress", "Completed")
            )

        call.respond(names)
    }

    get<SuggestionRoutes.ItemNamesRoute>({
        tags = listOf("suggestions")
        operationId = "item-names-suggestion"
        summary = "gets item name suggestions"
        response {
            HttpStatusCode.OK to {
                description = "suggested item names"
                body<NameSuggestionsDto>()
            }
        }
    }) {
        val names = SuggestionsDao.getItemNames()
            ?: NameSuggestionsDto(
                id = newIxIntId(),
                description = "names are missing",
                names = listOf("Bungee jumping", "Visit Prague")
            )

        call.respond(names)
    }

    get<SuggestionRoutes.TaskNamesRoute>({
        tags = listOf("suggestions")
        operationId = "task-names-suggestion"
        summary = "gets task name suggestions"
        response {
            HttpStatusCode.OK to {
                description = "suggested task names"
                body<NameSuggestionsDto>()
            }
        }
    }) {
        val names = SuggestionsDao.getTaskNames()
                ?: NameSuggestionsDto(
                    id = newIxIntId(),
                    description = "names are missing",
                    names = listOf("Bungee jumping", "Visit Prague")
                )

        call.respond(names)
    }
}
