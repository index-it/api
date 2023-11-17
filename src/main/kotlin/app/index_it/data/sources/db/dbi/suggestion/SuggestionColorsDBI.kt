package app.index_it.data.sources.db.dbi.suggestion

import app.index_it.core.logic.typedId.impl.IxIntId
import app.index_it.data.models.suggestions.ColorSuggestionsDto
import app.index_it.data.sources.db.dbi.DBI

interface SuggestionColorsDBI : DBI {
    suspend fun get(id: IxIntId<ColorSuggestionsDto>): ColorSuggestionsDto?
}