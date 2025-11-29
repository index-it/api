package app.index.api.routing.suggestion.routes

import app.index.api.routing.suggestion.SuggestionRoutes
import app.index.data.daos.suggestions.SuggestionsDao
import app.index.data.models.lists.CategoryData
import app.index.data.models.lists.ItemData
import app.index.data.models.lists.ListData
import app.index.data.models.tasks.TaskData
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.templatesRoute() {
    val suggestionsDao by inject<SuggestionsDao>()

    /**
     * gets a list template
     *
     * @tag templates
     * @operationId list-template
     * @query locale ISO 639 set 1 language code, `en` by default
     * @response 200 the list template
     * @response 401 user not authenticated
     */
    get<SuggestionRoutes.TemplateRoute.ListRoute> {
        val name = suggestionsDao.getRandomNameSuggestion(suggestionsDao.getListNames(it.locale))
        val color = suggestionsDao.getRandomColor()

        call.respond(ListData.ListTemplateResponseData(name, color))
    }

    /**
     * gets a category template
     *
     * @tag templates
     * @operationId category-template
     * @query locale ISO 639 set 1 language code, `en` by default
     * @response 200 the category template
     * @response 401 user not authenticated
     */
    get<SuggestionRoutes.TemplateRoute.CategoryRoute> {
        val name = suggestionsDao.getRandomNameSuggestion(suggestionsDao.getCategoryNames(it.locale))
        val color = suggestionsDao.getRandomColor()

        call.respond(CategoryData.CategoryTemplateResponseData(name, color))
    }

    /**
     * gets an item template
     *
     * @tag templates
     * @operationId item-template
     * @query locale ISO 639 set 1 language code, `en` by default
     * @response 200 the item template
     * @response 401 user not authenticated
     */
    get<SuggestionRoutes.TemplateRoute.ItemRoute> {
        val name = suggestionsDao.getRandomNameSuggestion(suggestionsDao.getItemNames(it.locale))

        call.respond(ItemData.ItemTemplateResponseData(name))
    }

    /**
     * gets a task template
     *
     * @tag templates
     * @operationId task-template
     * @query locale ISO 639 set 1 language code, `en` by default
     * @response 200 the task template
     * @response 401 user not authenticated
     */
    get<SuggestionRoutes.TemplateRoute.TaskRoute> {
        val name = suggestionsDao.getRandomNameSuggestion(suggestionsDao.getTaskNames(it.locale))

        call.respond(TaskData.TaskTemplateResponseData(name))
    }
}
