package app.index_it.data.sources.db.dbi.suggestion.impl

import app.index_it.core.logic.typedId.impl.IxIntId
import app.index_it.data.models.suggestions.NameSuggestionsDto
import app.index_it.data.sources.db.dbi.suggestion.SuggestionNamesDBI
import app.index_it.data.sources.db.schemas.suggestions.NameSuggestionEntity
import app.index_it.data.sources.db.toIxIntId

object SuggestionNamesDBIImpl : SuggestionNamesDBI {
    private fun NameSuggestionEntity.toDto() = NameSuggestionsDto(
        id = id.toIxIntId(),
        description = description,
        names = names.map { it.name }
    )

    override suspend fun get(id: IxIntId<NameSuggestionsDto>): NameSuggestionsDto? = dbQuery {
        NameSuggestionEntity.findById(id.id)?.toDto()
    }
}