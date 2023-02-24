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

/**
 * Represents an item in a list
 */
@Serializable
data class ItemDto(
    @Contextual @SerialName("_id") val id: Id<ItemDto> = ObjectId().toId(),
    @Contextual val user_id: Id<UserDto>,
    @Contextual val list_id: Id<ListDto>,
    val category_id: String,
    val name: String,
) {
    @Serializable
    data class ItemCreateRequestDto(
        val category_id: String,
        val name: String
    ): Validatable<ItemCreateRequestDto> {
        override fun validate() = Validation {
            ItemCreateRequestDto::name {
                minLength(1)
                maxLength(30)
            }
        }.invoke(this)
    }

    @Serializable
    data class ItemUpdateRequestDto(
        val category_id: String,
        val name: String?
    ): Validatable<ItemUpdateRequestDto> {
        override fun validate() = Validation {
            ItemUpdateRequestDto::name ifPresent {
                minLength(1)
                maxLength(30)
            }
        }.invoke(this)
    }
}
