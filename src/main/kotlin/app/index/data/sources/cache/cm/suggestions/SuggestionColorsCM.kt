package app.index.data.sources.cache.cm.suggestions

import app.index.core.logic.typedId.impl.IxIntId
import app.index.data.models.suggestions.ColorSuggestionsData

interface SuggestionColorsCM {
    fun cache(colorSuggestionsData: ColorSuggestionsData)

    fun get(id: IxIntId<ColorSuggestionsData>): ColorSuggestionsData?
}
