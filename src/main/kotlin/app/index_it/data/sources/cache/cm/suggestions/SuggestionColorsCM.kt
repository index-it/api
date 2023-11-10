package app.index_it.data.sources.cache.cm.suggestions

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.suggestions.ColorSuggestionsDto
import app.index_it.data.sources.cache.core.HashedCM

object SuggestionColorsCM : HashedCM("suggestion_colors") {
    fun cache(colorSuggestionsDto: ColorSuggestionsDto) = cache(colorSuggestionsDto.id.toString(), colorSuggestionsDto)

    fun get(id: IxId<ColorSuggestionsDto>) : ColorSuggestionsDto? = get(id.toString())
}
