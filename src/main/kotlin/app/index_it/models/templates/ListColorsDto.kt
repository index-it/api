package app.index_it.models.templates

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

@Serializable
data class ListColorsDto(
    @Contextual @SerialName("_id") val id: Id<ListColorsDto> = ObjectId().toId(),
    val description: String,
    val colors: List<String>
)
