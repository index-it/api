package app.index_it.api.routing.suggestion.routes

import app.index_it.api.routing.suggestion.SuggestionRoutes
import app.index_it.data.daos.suggestions.SuggestionsDao
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.tasks.TaskDto
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
                body<ListDto.ListTemplateResponseDto>()
            }
        }
    }) {
        val name = suggestionsDao.getRandomNameSuggestion(suggestionsDao.getListNames())
        val color = suggestionsDao.getRandomColor()

        call.respond(ListDto.ListTemplateResponseDto(name, color))
    }

    get<SuggestionRoutes.TemplateRoute.CategoryRoute>({
        tags = listOf("templates")
        operationId = "category-template"
        summary = "gets a category template"
        response {
            HttpStatusCode.OK to {
                description = "the category template"
                body<CategoryDto.CategoryTemplateResponseDto>()
            }
        }
    }) {
        val name = suggestionsDao.getRandomNameSuggestion(suggestionsDao.getCategoryNames())
        val color = suggestionsDao.getRandomColor()

        call.respond(CategoryDto.CategoryTemplateResponseDto(name, color))
    }

    get<SuggestionRoutes.TemplateRoute.ItemRoute>({
        tags = listOf("templates")
        operationId = "item-template"
        summary = "gets an item template"
        response {
            HttpStatusCode.OK to {
                description = "the item template"
                body<ItemDto.ItemTemplateResponseDto>()
            }
        }
    }) {
        val name = suggestionsDao.getRandomNameSuggestion(suggestionsDao.getItemNames())

        call.respond(ItemDto.ItemTemplateResponseDto(name))
    }

    get<SuggestionRoutes.TemplateRoute.TaskRoute>({
        tags = listOf("templates")
        operationId = "task-template"
        summary = "gets a task template"
        response {
            HttpStatusCode.OK to {
                description = "the task template"
                body<TaskDto.TaskTemplateResponseDto>()
            }
        }
    }) {
        val name = suggestionsDao.getRandomNameSuggestion(suggestionsDao.getTaskNames())

        call.respond(TaskDto.TaskTemplateResponseDto(name))
    }
}
