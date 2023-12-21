package app.index.data.sources.cache.cm.suggestions

import app.index.core.logic.typedId.impl.IxIntId
import app.index.data.models.suggestions.ColorSuggestionsDto

interface SuggestionColorsCM {
    fun cache(colorSuggestionsDto: ColorSuggestionsDto)

    fun get(id: IxIntId<ColorSuggestionsDto>): ColorSuggestionsDto?
}
