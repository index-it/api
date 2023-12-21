package app.index.data.sources.db.schemas.suggestions

import app.index.data.sources.db.core.array
import app.index.data.sources.db.schemas.suggestions.NameSuggestionTable.description
import app.index.data.sources.db.schemas.suggestions.NameSuggestionTable.id
import app.index.data.sources.db.schemas.suggestions.NameSuggestionTable.names
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.VarCharColumnType

/**
 * @property id
 * @property description
 * @property names
 */
object NameSuggestionTable : IntIdTable() {
    val description = varchar("description", 100)
    val names = array<String>("ix_names", VarCharColumnType(150))
}

/**
 * @property id
 * @property description
 * @property names
 */
class NameSuggestionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NameSuggestionEntity>(NameSuggestionTable)

    var description by NameSuggestionTable.description
    var names by NameSuggestionTable.names
}
