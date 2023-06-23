package app.index_it.models.templates

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

data class ListNamesDto(
    @Contextual @SerialName("_id") val id: Id<ListNamesDto> = ObjectId().toId(),
    val description: String,
    val names: List<String>
)
