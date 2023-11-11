package app.index_it.data.sources.db.schemas.suggestions

import app.index_it.data.sources.db.schemas.suggestions.NameSuggestionTable.description
import app.index_it.data.sources.db.schemas.suggestions.NameSuggestionTable.id
import app.index_it.data.sources.db.schemas.suggestions.NameTable.id
import app.index_it.data.sources.db.schemas.suggestions.NameTable.name
import app.index_it.data.sources.db.schemas.suggestions.NameTable.suggestion
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * @property id
 * @property description
 */
object NameSuggestionTable : IntIdTable() {
    val description = varchar("description", 100)
}

/**
 * @property id
 * @property description
 * @property names
 */
class NameSuggestionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NameSuggestionEntity>(NameSuggestionTable)

    val description by NameSuggestionTable.description
    val names by NameEntity referrersOn NameTable.suggestion
}


/**
 * @property id
 * @property suggestion
 * @property name
 */
object NameTable : IntIdTable() {
    val suggestion = reference(
        name = "suggestion",
        foreign = NameSuggestionTable,
        onDelete = ReferenceOption.CASCADE
    )
    val name = varchar("name", 150)
}

/**
 * @property id
 * @property suggestion
 * @property name
 */
class NameEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NameEntity>(NameTable)

    val suggestion by NameSuggestionEntity referencedOn NameTable.suggestion
    val name by NameTable.name
}