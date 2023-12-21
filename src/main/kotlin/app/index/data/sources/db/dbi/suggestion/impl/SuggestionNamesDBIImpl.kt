package app.index.data.sources.db.dbi.suggestion.impl

import app.index.core.logic.typedId.impl.IxIntId
import app.index.data.models.suggestions.NameSuggestionsDto
import app.index.data.sources.db.dbi.suggestion.SuggestionNamesDBI
import app.index.data.sources.db.schemas.suggestions.NameSuggestionEntity
import app.index.data.sources.db.toIxIntId
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class SuggestionNamesDBIImpl : SuggestionNamesDBI {
    private fun NameSuggestionEntity.toDto() =
        NameSuggestionsDto(
            id = id.toIxIntId(),
            description = description,
            names = names.toList(),
        )

    override suspend fun get(id: IxIntId<NameSuggestionsDto>): NameSuggestionsDto? =
        dbQuery {
            NameSuggestionEntity.findById(id.id)?.toDto()
        }
}
