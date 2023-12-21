package app.index.data.sources.db.schemas.suggestions

import app.index.data.sources.db.core.array
import app.index.data.sources.db.schemas.suggestions.NameSuggestionTable.description
import app.index.data.sources.db.schemas.suggestions.NameSuggestionTable.id
import app.index.data.sources.db.schemas.suggestions.NameSuggestionTable.names
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.VarCharColumnType

/**
 * @property id
 * @property description
 * @property names
 * @property locale
 */
object NameSuggestionTable : Table() {
    val id = integer("id").autoIncrement().index()
    val locale = char("locale", 2).index()
    val description = varchar("description", 100)
    val names = array<String>("ix_names", VarCharColumnType(150))

    override val primaryKey = PrimaryKey(id, locale)
}
