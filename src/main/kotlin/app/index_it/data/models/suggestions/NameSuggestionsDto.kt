package app.index_it.data.models.suggestions

import app.index_it.data.sources.db.schemas.suggestions.NameSuggestionSchema
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

@Serializable
data class NameSuggestionsDto(
    @Contextual @SerialName("_id") val id: Id<NameSuggestionsDto> = ObjectId().toId(),
    val description: String,
    val names: List<String>
)
