package app.index_it.data.models.suggestions

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

@Serializable
data class ColorSuggestionsDto(
    @Contextual @SerialName("_id") val id: Id<ColorSuggestionsDto> = ObjectId().toId(),
    val description: String,
    val colors: List<String>
)
