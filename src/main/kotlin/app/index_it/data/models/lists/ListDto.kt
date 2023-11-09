package app.index_it.models.lists

import app.index_it.core.logic.RegexPatterns
import app.index_it.core.logic.currentMillis
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
data class ListDto(
    @Contextual @SerialName("_id") val id: Id<ListDto> = ObjectId().toId(),
    @Contextual var userId: Id<UserDto>,
    var name: String,
    var icon: String, // Single emoji at the moment
    var color: String, // Represented as #RRGGBB hex color
    @SerialName("created_at")
    val createdAt: Long = currentMillis(),
    @SerialName("edited_at")
    val editedAt: Long? = null
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
        var name: String,
        var icon: String,
        var color: String
    ): Validatable<ListUpdateRequestDto> {
        override fun validate() = Validation {
            ListUpdateRequestDto::name {
                minLength(1)
                maxLength(50)
            }
            // TODO: Icon validation
            ListUpdateRequestDto::color {
                pattern(RegexPatterns.colorPattern)
            }
        }.invoke(this)
    }

    @Serializable
    data class ListTemplateResponseDto(
        val name: String,
        val color: String
    )
}
