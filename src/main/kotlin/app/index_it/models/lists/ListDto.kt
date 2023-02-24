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
 * Represents a single list, which can contain categories to organize list items in it
 */
@Serializable
@Suppress("PropertyName")
data class ListDto(
    @Contextual @SerialName("_id") val id: Id<ListDto> = ObjectId().toId(),
    @Contextual var user_id: Id<UserDto>,
    var name: String,
    val categories: MutableList<CategoryDto> = mutableListOf(),
    var icon: String,
    var color: String
) {
    @Serializable
    data class ListCreateRequestDto(
        var name: String,
        var icon: String,
        var color: String
    ): Validatable<ListCreateRequestDto> {
        override fun validate() = Validation {
            ListCreateRequestDto::name {
                minLength(1)
                maxLength(50)
            }
            // TODO: Decide the format of the other properties
        }.invoke(this)
    }

    @Serializable
    data class ListUpdateRequestDto(
        var name: String?,
        var icon: String?,
        var color: String?
    ): Validatable<ListUpdateRequestDto> {
        override fun validate() = Validation {
            ListUpdateRequestDto::name ifPresent {
                minLength(1)
                maxLength(50)
            }
            // TODO: Decide the format of the other properties
        }.invoke(this)
    }
}
