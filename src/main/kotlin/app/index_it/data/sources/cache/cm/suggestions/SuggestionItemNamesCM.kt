package app.index_it.data.sources.cache.cm.suggestions

import app.index_it.core.logic.typedId.impl.IxIntId
import app.index_it.data.models.suggestions.NameSuggestionsDto

interface SuggestionItemNamesCM {
    fun cache(nameSuggestionsDto: NameSuggestionsDto)

    fun get(id: IxIntId<NameSuggestionsDto>) : NameSuggestionsDto?
}