package app.index_it.data.sources.db.schemas.suggestions

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object ColorSuggestionTable : IntIdTable() {
    val description = varchar("description", 100)
}

class ColorSuggestionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ColorSuggestionEntity>(ColorSuggestionTable)

    val description by ColorSuggestionTable.description
    val colors by ColorEntity referrersOn ColorTable.suggestion
}


object ColorTable : IntIdTable() {
    val suggestion = reference("suggestion", ColorSuggestionTable)
    val color = char("color", 9)
}

class ColorEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ColorEntity>(ColorTable)

    val suggestion by ColorSuggestionEntity referencedOn ColorTable.suggestion
    val color by ColorTable.color
}