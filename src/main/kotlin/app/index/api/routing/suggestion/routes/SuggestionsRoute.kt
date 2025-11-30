package app.index.api.routing.suggestion.routes

import app.index.api.routing.suggestion.SuggestionRoutes
import app.index.core.logic.typedId.newIxIntId
import app.index.data.daos.suggestions.SuggestionsDao
import app.index.data.models.suggestions.ColorSuggestionsData
import app.index.data.models.suggestions.NameSuggestionsData
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.suggestionsRoute() {
    val suggestionDao by inject<SuggestionsDao>()

    /**
     * gets color suggestions
     *
     * @tag suggestions
     * @operationId colors-suggestion
     * @response 200 suggested colors
     * @response 401 user not authenticated
     */
    get<SuggestionRoutes.ColorsRoute> {
        val colors = suggestionDao.getColors()
            ?: ColorSuggestionsData(
                id = newIxIntId(),
                description = "colors are missing",
                colors = listOf("#000000", "#FFFFFF"),
            )

        call.respond(colors)
    }

    /**
     * gets list name suggestions
     *
     * @tag suggestions
     * @operationId list-names-suggestion
     * @query locale ISO 639 set 1 language code, `en` by default
     * @response 200 suggested list names
     * @response 401 user not authenticated
     */
    get<SuggestionRoutes.ListNamesRoute> {
        val names = suggestionDao.getListNames(it.locale)
            ?: NameSuggestionsData(
                id = newIxIntId(),
                description = "names are missing",
                names = listOf("Vacations"),
                locale = "en"
            )

        call.respond(names)
    }

    /**
     * gets category name suggestions
     *
     * @tag suggestions
     * @operationId category-names-suggestion
     * @query locale ISO 639 set 1 language code, `en` by default
     * @response 200 suggested category names
     * @response 401 user not authenticated
     */
    get<SuggestionRoutes.CategoryNamesRoute> {
        val names = suggestionDao.getCategoryNames(it.locale)
            ?: NameSuggestionsData(
                id = newIxIntId(),
                description = "names are missing",
                names = listOf("Planned", "In progress", "Completed"),
                locale = "en"
            )

        call.respond(names)
    }

    /**
     * gets item name suggestions
     *
     * @tag suggestions
     * @operationId item-names-suggestion
     * @query locale ISO 639 set 1 language code, `en` by default
     * @response 200 suggested item names
     * @response 401 user not authenticated
     */
    get<SuggestionRoutes.ItemNamesRoute> {
        val names = suggestionDao.getItemNames(it.locale)
            ?: NameSuggestionsData(
                id = newIxIntId(),
                description = "names are missing",
                names = listOf("Bungee jumping", "Visit Prague"),
                locale = "en"
            )

        call.respond(names)
    }

    /**
     * gets task name suggestions
     *
     * @tag suggestions
     * @operationId task-names-suggestion
     * @query locale ISO 639 set 1 language code, `en` by default
     * @response 200 suggested task names
     * @response 401 user not authenticated
     */
    get<SuggestionRoutes.TaskNamesRoute> {
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
