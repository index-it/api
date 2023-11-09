package app.index_it.data.sources.db.schemas.suggestions

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.jetbrains.exposed.dao.id.IntIdTable
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

object NameSuggestionTable : IntIdTable() {
    val description = varchar("description", 100)
    // TODO: names
}

@Serializable
data class NameSuggestionSchema(
    @Contextual @SerialName("_id") val id: Id<NameSuggestionSchema> = ObjectId().toId(),
    val description: String,
    val names: List<String>
)
