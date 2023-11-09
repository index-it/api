package app.index_it.data.sources.db.schemas.suggestions

import org.jetbrains.exposed.dao.id.IntIdTable

object ColorSuggestionTable : IntIdTable() {
    val description = varchar("description", 100)
    // TODO: val colors =
}