package app.index.data.models.lists

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.RegexPatterns
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.Validatable
import app.index.data.models.user.UserDto
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import io.konform.validation.jsonschema.pattern
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single list, which can contain categories to organize list items in it
 */
@Serializable
data class ListDto(
    @Contextual val id: IxId<ListDto>,
    @Contextual var userId: IxId<UserDto>,
    var name: String,
    var icon: String, // Single emoji at the moment
    var color: String, // Represented as #RRGGBB hex color
    @SerialName("created_at")
    val createdAt: Long = DatetimeUtils.currentMillis(),
    @SerialName("edited_at")
    val editedAt: Long? = null,
) {
    @Serializable
    data class ListCreateRequestDto(
        var name: String,
        var icon: String,
        var color: String,
    ) : Validatable<ListCreateRequestDto> {
        override fun validate() =
            Validation {
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
        var color: String,
    ) : Validatable<ListUpdateRequestDto> {
        override fun validate() =
            Validation {
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
        val color: String,
    )
}
