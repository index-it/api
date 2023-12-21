package app.index.data.sources.db.dbi.suggestion.impl

import app.index.core.logic.typedId.impl.IxIntId
import app.index.data.models.suggestions.ColorSuggestionsData
import app.index.data.sources.db.dbi.suggestion.SuggestionColorsDBI
import app.index.data.sources.db.schemas.suggestions.ColorSuggestionEntity
import app.index.data.sources.db.toIxIntId
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class SuggestionColorsDBIImpl : SuggestionColorsDBI {
    private fun ColorSuggestionEntity.toDto() =
        ColorSuggestionsData(
            id = id.toIxIntId(),
            description = description,
            colors = colors.toList(),
        )

    override suspend fun get(id: IxIntId<ColorSuggestionsData>): ColorSuggestionsData? =
        dbQuery {
            ColorSuggestionEntity.findById(id.id)?.toDto()
        }
}
