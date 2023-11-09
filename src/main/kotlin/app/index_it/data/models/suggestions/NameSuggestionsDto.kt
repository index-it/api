package app.index_it.data.models.suggestions

import app.index_it.data.sources.db.schemas.suggestions.ColorSuggestionEntity.Companion.referrersOn
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

object NameSuggestionTable : IntIdTable() {
    val description = varchar("description", 100)
}

class NameSuggestionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NameSuggestionEntity>(NameSuggestionTable)

    val description by NameSuggestionTable.description
    val names by NameEntity referrersOn NameTable.suggestion
}


object NameTable : IntIdTable() {
    val suggestion = reference("suggestion", NameSuggestionTable)
    val name = varchar("name", 150)
}

class NameEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NameEntity>(NameTable)

    val suggestion by NameSuggestionEntity referencedOn NameTable.suggestion
    val name by NameTable.name
}