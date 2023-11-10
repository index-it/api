package app.index_it.data.sources.db.dbi.suggestion

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.suggestions.NameSuggestionsDto
import app.index_it.data.sources.db.dbi.DBI

interface SuggestionItemNamesDBI : DBI {
    suspend fun get(id: IxId<NameSuggestionsDto>): NameSuggestionsDto?
}