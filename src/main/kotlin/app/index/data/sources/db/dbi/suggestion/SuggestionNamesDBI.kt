package app.index.data.sources.db.dbi.suggestion

import app.index.core.logic.typedId.impl.IxIntId
import app.index.data.models.suggestions.NameSuggestionsData
import app.index.data.sources.db.dbi.DBI

interface SuggestionNamesDBI : DBI {
    suspend fun get(id: IxIntId<NameSuggestionsData>, locale: String): NameSuggestionsData?
}
