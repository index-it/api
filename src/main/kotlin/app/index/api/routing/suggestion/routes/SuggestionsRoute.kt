package app.index.api.routing.suggestion.routes

import app.index.api.routing.suggestion.SuggestionRoutes
import app.index.core.logic.typedId.newIxIntId
import app.index.data.daos.suggestions.SuggestionsDao
import app.index.data.models.suggestions.ColorSuggestionsData
import app.index.data.models.suggestions.NameSuggestionsData
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.suggestionsRoute() {
    val suggestionDao by inject<SuggestionsDao>()

    get<SuggestionRoutes.ColorsRoute>({
        tags = listOf("suggestions")
        operationId = "colors-suggestion"
        summary = "gets color suggestions"
        response {
            HttpStatusCode.OK to {
                description = "suggested colors"
                body<ColorSuggestionsData>()
            }
        }
    }) {
        val colors = suggestionDao.getColors()
            ?: ColorSuggestionsData(
                id = newIxIntId(),
                description = "colors are missing",
                colors = listOf("#000000", "#FFFFFF"),
            )

        call.respond(colors)
    }

    get<SuggestionRoutes.ListNamesRoute>({
        tags = listOf("suggestions")
        operationId = "list-names-suggestion"
        summary = "gets list name suggestions"
        request {
            queryParameter<String>("locale") {
                description = "ISO 639 set 1 language code"
                required = true
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "suggested list names"
                body<NameSuggestionsData>()
            }
        }
    }) {
        val names = suggestionDao.getListNames(it.locale)
            ?: NameSuggestionsData(
                id = newIxIntId(),
                description = "names are missing",
                names = listOf("Vacations"),
                locale = "en"
            )

        call.respond(names)
    }

    get<SuggestionRoutes.CategoryNamesRoute>({
        tags = listOf("suggestions")
        operationId = "category-names-suggestion"
        summary = "gets category name suggestions"
        request {
            queryParameter<String>("locale") {
                description = "ISO 639 set 1 language code"
                required = true
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "suggested category names"
                body<NameSuggestionsData>()
            }
        }
    }) {
        val names = suggestionDao.getCategoryNames(it.locale)
            ?: NameSuggestionsData(
                id = newIxIntId(),
                description = "names are missing",
                names = listOf("Planned", "In progress", "Completed"),
                locale = "en"
            )

        call.respond(names)
    }

    get<SuggestionRoutes.ItemNamesRoute>({
        tags = listOf("suggestions")
        operationId = "item-names-suggestion"
        summary = "gets item name suggestions"
        request {
            queryParameter<String>("locale") {
                description = "ISO 639 set 1 language code"
                required = true
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "suggested item names"
                body<NameSuggestionsData>()
            }
        }
    }) {
        val names = suggestionDao.getItemNames(it.locale)
            ?: NameSuggestionsData(
                id = newIxIntId(),
                description = "names are missing",
                names = listOf("Bungee jumping", "Visit Prague"),
                locale = "en"
            )

        call.respond(names)
    }

    get<SuggestionRoutes.TaskNamesRoute>({
        tags = listOf("suggestions")
        operationId = "task-names-suggestion"
        summary = "gets task name suggestions"
        request {
            queryParameter<String>("locale") {
                description = "ISO 639 set 1 language code"
                required = true
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "suggested task names"
                body<NameSuggestionsData>()
            }
        }
    }) {
        val names = suggestionDao.getTaskNames(it.locale)
            ?: NameSuggestionsData(
                id = newIxIntId(),
                description = "names are missing",
                names = listOf("Bungee jumping", "Visit Prague"),
                locale = "en"
            )

        call.respond(names)
    }
}
