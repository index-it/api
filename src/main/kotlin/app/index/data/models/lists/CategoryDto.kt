package app.index.data.models.lists

import app.index.core.logic.RegexPatterns
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.Validatable
import app.index.data.models.user.UserDto
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import io.konform.validation.jsonschema.pattern
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Groups items in a list, for example, a list of movies to watch can have categories for the genre.
 * 'name' is the category ID
 */
@Serializable
data class CategoryDto(
    @Contextual val id: IxId<CategoryDto>,
    @Contextual val userId: IxId<UserDto>,
    @Contextual val listId: IxId<ListDto>,
    var name: String,
    var color: String, // Represented as #010101 hex color
) {
    @Serializable
    data class CategoryCreateRequestDto(
        val name: String,
        val color: String,
    ) : Validatable<CategoryCreateRequestDto> {
        override fun validate() =
            Validation {
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
        val color: String,
    ) : Validatable<CategoryUpdateRequestDto> {
        override fun validate() =
            Validation {
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
        val color: String,
    )
}
