package app.index_it.core.cache

import app.index_it.models.suggestions.ListNameSuggestionsDto
import org.litote.kmongo.Id

object SuggestionListNamesCM: HashedCM("suggestion_list_names") {
    fun cache(listNameSuggestionsDto: ListNameSuggestionsDto) = cache(listNameSuggestionsDto.id.toString(), listNameSuggestionsDto)

    fun get(id: Id<ListNameSuggestionsDto>) : ListNameSuggestionsDto? = get(id.toString())
}
