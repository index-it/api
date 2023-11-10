package app.index_it.data.sources.cache.cm.suggestions

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.suggestions.NameSuggestionsDto
import app.index_it.data.sources.cache.core.HashedCM

object SuggestionTaskNamesCM: HashedCM("suggestion_task_names") {
    fun cache(nameSuggestionsDto: NameSuggestionsDto) = cache(nameSuggestionsDto.id.toString(), nameSuggestionsDto)

    fun get(id: IxId<NameSuggestionsDto>) : NameSuggestionsDto? = get(id.toString())
}
