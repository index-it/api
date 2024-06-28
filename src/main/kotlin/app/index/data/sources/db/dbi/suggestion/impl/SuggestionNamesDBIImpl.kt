package app.index.data.sources.db.dbi.suggestion.impl

import app.index.core.logic.typedId.impl.IxIntId
import app.index.core.logic.typedId.toIxIntId
import app.index.data.models.suggestions.NameSuggestionsData
import app.index.data.sources.db.dbi.suggestion.SuggestionNamesDBI
import app.index.data.sources.db.schemas.suggestions.NameSuggestionTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class SuggestionNamesDBIImpl : SuggestionNamesDBI {
    override suspend fun get(id: IxIntId<NameSuggestionsData>, locale: String): NameSuggestionsData? {
        return dbQuery {
            NameSuggestionTable.selectAll()
                .where { (NameSuggestionTable.id eq id.id) and (NameSuggestionTable.locale eq locale) }
                .limit(1)
                .firstOrNull()
        }?.let {
            NameSuggestionsData(
                id = it[NameSuggestionTable.id].toIxIntId(),
                description = it[NameSuggestionTable.description],
                names = it[NameSuggestionTable.names].toList(),
                locale = it[NameSuggestionTable.locale]
            )
        }
    }
}
