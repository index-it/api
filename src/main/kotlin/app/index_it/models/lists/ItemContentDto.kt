package app.index_it.models.lists

import app.index_it.models.Validatable
import app.index_it.models.user.UserDto
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

@Serializable
data class ItemContentDto(
    @Contextual @SerialName("_id") val id: Id<ItemContentDto> = ObjectId().toId(),
    @Contextual val userId: Id<UserDto>,
    @Contextual val itemId: Id<ItemDto>,
    val content: String,
) {
    @Serializable
    data class ItemContentCreateOrUpdateRequest(
        val content: String
    ): Validatable<ItemContentCreateOrUpdateRequest> {
        override fun validate() = Validation {
            ItemContentCreateOrUpdateRequest::content {
                minLength(1)
                maxLength(10000)
            }
        }.invoke(this)
    }
}
