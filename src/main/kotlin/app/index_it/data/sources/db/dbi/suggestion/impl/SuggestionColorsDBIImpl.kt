package app.index_it.data.sources.db.dbi.suggestion.impl

import app.index_it.core.logic.typedId.impl.IxIntId
import app.index_it.data.models.suggestions.ColorSuggestionsDto
import app.index_it.data.sources.db.dbi.suggestion.SuggestionColorsDBI
import app.index_it.data.sources.db.schemas.suggestions.ColorSuggestionEntity
import app.index_it.data.sources.db.toIxIntId

object SuggestionColorsDBIImpl : SuggestionColorsDBI {
    private fun ColorSuggestionEntity.toDto() = ColorSuggestionsDto(
        id = id.toIxIntId(),
        description = description,
        colors = colors.map { it.color }
    )

    override suspend fun get(id: IxIntId<ColorSuggestionsDto>): ColorSuggestionsDto? = dbQuery {
        ColorSuggestionEntity.findById(id.id)?.toDto()
    }

}