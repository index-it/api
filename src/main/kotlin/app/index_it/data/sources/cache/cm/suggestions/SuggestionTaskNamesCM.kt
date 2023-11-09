package app.index_it.data.sources.cache.cm.suggestions

import app.index_it.data.sources.cache.core.HashedCM
import app.index_it.data.models.suggestions.NameSuggestionsDto
import org.litote.kmongo.Id

object SuggestionTaskNamesCM: HashedCM("suggestion_task_names") {
    fun cache(nameSuggestionsDto: NameSuggestionsDto) = cache(nameSuggestionsDto.id.toString(), nameSuggestionsDto)

    fun get(id: Id<NameSuggestionsDto>) : NameSuggestionsDto? = get(id.toString())
}
