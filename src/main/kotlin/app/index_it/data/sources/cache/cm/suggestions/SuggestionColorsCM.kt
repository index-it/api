package app.index_it.data.sources.cache.cm.suggestions

import app.index_it.core.logic.typedId.impl.IxIntId
import app.index_it.data.models.suggestions.ColorSuggestionsDto

interface SuggestionColorsCM {
    fun cache(colorSuggestionsDto: ColorSuggestionsDto)

    fun get(id: IxIntId<ColorSuggestionsDto>) : ColorSuggestionsDto?
}