package app.index.api.routing.suggestion.routes

import app.index.api.routing.suggestion.SuggestionRoutes
import app.index.data.daos.suggestions.SuggestionsDao
import app.index.data.models.lists.CategoryData
import app.index.data.models.lists.ItemData
import app.index.data.models.lists.ListData
import app.index.data.models.tasks.TaskData
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.templatesRoute() {
    val suggestionsDao by inject<SuggestionsDao>()

    get<SuggestionRoutes.TemplateRoute.ListRoute>({
        tags = listOf("templates")
        operationId = "list-template"
        summary = "gets a list template"
        response {
            HttpStatusCode.OK to {
                description = "the list template"
                body<ListData.ListTemplateResponseData>()
            }
        }
    }) {
        val name = suggestionsDao.getRandomNameSuggestion(suggestionsDao.getListNames())
        val color = suggestionsDao.getRandomColor()

        call.respond(ListData.ListTemplateResponseData(name, color))
    }

    get<SuggestionRoutes.TemplateRoute.CategoryRoute>({
        tags = listOf("templates")
        operationId = "category-template"
        summary = "gets a category template"
        response {
            HttpStatusCode.OK to {
                description = "the category template"
                body<CategoryData.CategoryTemplateResponseData>()
            }
        }
    }) {
        val name = suggestionsDao.getRandomNameSuggestion(suggestionsDao.getCategoryNames())
        val color = suggestionsDao.getRandomColor()

        call.respond(CategoryData.CategoryTemplateResponseData(name, color))
    }

    get<SuggestionRoutes.TemplateRoute.ItemRoute>({
        tags = listOf("templates")
        operationId = "item-template"
        summary = "gets an item template"
        response {
            HttpStatusCode.OK to {
                description = "the item template"
                body<ItemData.ItemTemplateResponseData>()
            }
        }
    }) {
        val name = suggestionsDao.getRandomNameSuggestion(suggestionsDao.getItemNames())

        call.respond(ItemData.ItemTemplateResponseData(name))
    }

    get<SuggestionRoutes.TemplateRoute.TaskRoute>({
        tags = listOf("templates")
        operationId = "task-template"
        summary = "gets a task template"
        response {
            HttpStatusCode.OK to {
                description = "the task template"
                body<TaskData.TaskTemplateResponseData>()
            }
        }
    }) {
        val name = suggestionsDao.getRandomNameSuggestion(suggestionsDao.getTaskNames())

        call.respond(TaskData.TaskTemplateResponseData(name))
    }
}
