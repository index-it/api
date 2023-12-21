package app.index.data.sources.db.dbi.suggestion

import app.index.core.logic.typedId.impl.IxIntId
import app.index.data.models.suggestions.ColorSuggestionsDto
import app.index.data.sources.db.dbi.DBI

interface SuggestionColorsDBI : DBI {
    suspend fun get(id: IxIntId<ColorSuggestionsDto>): ColorSuggestionsDto?
}
