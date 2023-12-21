package app.index.data.sources.db.dbi.suggestion

import app.index.core.logic.typedId.impl.IxIntId
import app.index.data.models.suggestions.NameSuggestionsDto
import app.index.data.sources.db.dbi.DBI

interface SuggestionNamesDBI : DBI {
    suspend fun get(id: IxIntId<NameSuggestionsDto>): NameSuggestionsDto?
}
