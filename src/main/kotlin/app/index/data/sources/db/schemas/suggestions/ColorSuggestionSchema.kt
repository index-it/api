package app.index.data.sources.db.schemas.suggestions

import app.index.data.sources.db.schemas.suggestions.ColorSuggestionTable.colors
import app.index.data.sources.db.schemas.suggestions.ColorSuggestionTable.description
import app.index.data.sources.db.schemas.suggestions.ColorSuggestionTable.id
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.VarCharColumnType

/**
 * @property id
 * @property description
 * @property colors
 */
object ColorSuggestionTable : IntIdTable() {
    val description = varchar("description", 100)
    val colors = array<String>("colors", VarCharColumnType(9))
}

/**
 * @property id
 * @property description
 * @property colors
 */
class ColorSuggestionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ColorSuggestionEntity>(ColorSuggestionTable)

    var description by ColorSuggestionTable.description
    var colors by ColorSuggestionTable.colors
}
