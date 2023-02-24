package app.index_it.models.lists

import app.index_it.core.logic.RegexPatterns
import app.index_it.models.Validatable
import app.index_it.models.user.UserDto
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import io.konform.validation.jsonschema.pattern
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
    var icon: String, // TODO: Define representation
    var color: String // Represented as 0xFF010101 Includes opacity and hex color
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
            ListCreateRequestDto::color {
                pattern(RegexPatterns.colorPattern)
            }
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
            ListUpdateRequestDto::color ifPresent {
                pattern(RegexPatterns.colorPattern)
            }
        }.invoke(this)
    }
}
