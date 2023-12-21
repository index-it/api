package app.index.data.sources.cache.cm.suggestions

import app.index.core.logic.typedId.impl.IxIntId
import app.index.data.models.suggestions.NameSuggestionsData

interface SuggestionCategoryNamesCM {
    fun cache(nameSuggestionsData: NameSuggestionsData)

    fun get(id: IxIntId<NameSuggestionsData>, locale: String): NameSuggestionsData?
}
