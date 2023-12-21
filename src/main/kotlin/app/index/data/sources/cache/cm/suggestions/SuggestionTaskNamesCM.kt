package app.index.data.sources.cache.cm.suggestions

import app.index.core.logic.typedId.impl.IxIntId
import app.index.data.models.suggestions.NameSuggestionsDto

interface SuggestionTaskNamesCM {
    fun cache(nameSuggestionsDto: NameSuggestionsDto)

    fun get(id: IxIntId<NameSuggestionsDto>): NameSuggestionsDto?
}
