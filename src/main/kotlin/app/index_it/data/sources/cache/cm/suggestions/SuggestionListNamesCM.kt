package app.index_it.data.sources.cache.cm.suggestions

import app.index_it.core.logic.typedId.impl.IxIntId
import app.index_it.data.models.suggestions.NameSuggestionsDto
import app.index_it.data.sources.cache.core.HashedCM

object SuggestionListNamesCM: HashedCM("suggestion_list_names") {
    fun cache(nameSuggestionsDto: NameSuggestionsDto) = cache(nameSuggestionsDto.id.toString(), nameSuggestionsDto)

    fun get(id: IxIntId<NameSuggestionsDto>) : NameSuggestionsDto? = get(id.toString())
}
