package app.index_it.models.suggestions

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

@Serializable
data class ListNameSuggestionsDto(
    @Contextual @SerialName("_id") val id: Id<ListNameSuggestionsDto> = ObjectId().toId(),
    val description: String,
    val names: List<String>
)
