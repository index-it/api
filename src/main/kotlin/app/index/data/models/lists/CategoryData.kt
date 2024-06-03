package app.index.data.models.lists

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserData
import app.index.data.validation.RegexPatterns
import app.index.data.validation.Validatable
import app.index.data.validation.Validations
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import io.konform.validation.jsonschema.pattern
import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Groups items in a list, for example, a list of movies to watch can have categories for the genre
 */
@Serializable
data class CategoryData(
    @field:Schema(required = true)
    @Contextual val id: IxId<CategoryData>,
    @field:Schema(required = true)
    @Contextual val user_id: IxId<UserData>,
    @field:Schema(required = true)
    @Contextual val list_id: IxId<ListData>,
    @field:Schema(required = true)
    var name: String,
    @field:Schema(required = true)
    var color: String, // Represented as #010101 hex color
    @field:Schema(required = true)
    val created_at: Long = DatetimeUtils.currentMillis(),
    val edited_at: Long? = null,
) {
    @Serializable
    data class CategoryCreateRequestData(
        @field:Schema(required = true)
        val name: String,
        @field:Schema(required = true)
        val color: String,
    ) : Validatable<CategoryCreateRequestData> {
        override fun validate() =
            Validation {
                CategoryCreateRequestData::name {
                    minLength(Validations.Category.MIN_NAME_LENGTH)
                    maxLength(Validations.Category.MAX_NAME_LENGTH)
                }
                CategoryCreateRequestData::color {
                    pattern(RegexPatterns.colorPattern)
                }
            }.invoke(this)
    }

    @Serializable
    data class CategoryUpdateRequestData(
        @field:Schema(required = true)
        val name: String,
        @field:Schema(required = true)
        val color: String,
    ) : Validatable<CategoryUpdateRequestData> {
        override fun validate() =
            Validation {
                CategoryUpdateRequestData::name {
                    minLength(Validations.Category.MIN_NAME_LENGTH)
                    maxLength(Validations.Category.MAX_NAME_LENGTH)
                }
                CategoryUpdateRequestData::color {
                    pattern(RegexPatterns.colorPattern)
                }
            }.invoke(this)
    }

    @Serializable
    data class CategoryTemplateResponseData(
        @field:Schema(required = true)
        val name: String,
        @field:Schema(required = true)
        val color: String,
    )
}
