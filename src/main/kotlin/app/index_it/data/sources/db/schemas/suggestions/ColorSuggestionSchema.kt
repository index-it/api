package app.index_it.data.sources.db.schemas.suggestions

import app.index_it.data.sources.db.schemas.suggestions.ColorSuggestionTable.description
import app.index_it.data.sources.db.schemas.suggestions.ColorSuggestionTable.id
import app.index_it.data.sources.db.schemas.suggestions.ColorTable.color
import app.index_it.data.sources.db.schemas.suggestions.ColorTable.id
import app.index_it.data.sources.db.schemas.suggestions.ColorTable.suggestion
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * @property id
 * @property description
 */
object ColorSuggestionTable : IntIdTable() {
    val description = varchar("description", 100)
}

/**
 * @property id
 * @property description
 * @property colors
 */
class ColorSuggestionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ColorSuggestionEntity>(ColorSuggestionTable)

    val description by ColorSuggestionTable.description
    val colors by ColorEntity referrersOn ColorTable.suggestion
}


/**
 * @property id
 * @property suggestion
 * @property color
 */
object ColorTable : IntIdTable() {
    val suggestion = reference(
        name = "suggestion",
        foreign = ColorSuggestionTable,
        onDelete = ReferenceOption.CASCADE
    )
    val color = char("color", 9)
}

/**
 * @property id
 * @property suggestion
 * @property color
 */
class ColorEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ColorEntity>(ColorTable)

    val suggestion by ColorSuggestionEntity referencedOn ColorTable.suggestion
    val color by ColorTable.color
}