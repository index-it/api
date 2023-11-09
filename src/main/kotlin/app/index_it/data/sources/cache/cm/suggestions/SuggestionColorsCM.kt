package app.index_it.data.sources.cache.suggestions

import app.index_it.core.cache.core.HashedCM
import app.index_it.models.suggestions.ColorSuggestionsDto
import org.litote.kmongo.Id

object SuggestionColorsCM : HashedCM("suggestion_colors") {
    fun cache(colorSuggestionsDto: ColorSuggestionsDto) = cache(colorSuggestionsDto.id.toString(), colorSuggestionsDto)

    fun get(id: Id<ColorSuggestionsDto>) : ColorSuggestionsDto? = get(id.toString())
}
