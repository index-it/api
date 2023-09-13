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
 * Groups items in a list, for example, a list of movies to watch can have categories for the genre.
 * 'name' is the category ID
 */
@Serializable
data class CategoryDto(
    @Contextual @SerialName("_id") val id: Id<CategoryDto> = ObjectId().toId(),
    @Contextual val userId: Id<UserDto>,
    @Contextual val listId: Id<ListDto>,
    var name: String,
    var color: String // Represented as #010101 hex color
) {
    @Serializable
    data class CategoryCreateRequestDto(
        val name: String,
        val color: String
    ): Validatable<CategoryCreateRequestDto> {
        override fun validate() = Validation {
            CategoryCreateRequestDto::name {
                minLength(1)
                maxLength(30)
            }
            CategoryCreateRequestDto::color {
                pattern(RegexPatterns.colorPattern)
            }
        }.invoke(this)
    }

    @Serializable
    data class CategoryUpdateRequestDto(
        val name: String,
        val color: String
    ): Validatable<CategoryUpdateRequestDto> {
        override fun validate() = Validation {
            CategoryUpdateRequestDto::name {
                minLength(1)
                maxLength(30)
            }
            CategoryUpdateRequestDto::color {
                pattern(RegexPatterns.colorPattern)
            }
        }.invoke(this)
    }

    @Serializable
    data class CategoryTemplateResponseDto(
        val name: String,
        val color: String
    )
}
