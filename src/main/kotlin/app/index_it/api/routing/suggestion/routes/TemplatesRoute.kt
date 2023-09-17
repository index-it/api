package app.index_it.api.routing.suggestion.routes

import app.index_it.api.routing.suggestion.SuggestionRoutes
import app.index_it.daos.suggestions.SuggestionsDao
import app.index_it.models.lists.CategoryDto
import app.index_it.models.lists.ItemDto
import app.index_it.models.lists.ListDto
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.templatesRoute() {
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
        val name = SuggestionsDao.getRandomNameSuggestion(SuggestionsDao.getListNames())
        val color = SuggestionsDao.getRandomColor()

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
        val name = SuggestionsDao.getRandomNameSuggestion(SuggestionsDao.getCategoryNames())
        val color = SuggestionsDao.getRandomColor()

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
        val name = SuggestionsDao.getRandomNameSuggestion(SuggestionsDao.getItemNames())

        call.respond(ItemDto.ItemTemplateResponseDto(name))
    }
}
