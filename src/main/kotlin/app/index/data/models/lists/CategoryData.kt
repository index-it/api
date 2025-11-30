package app.index.data.models.lists

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserData
import app.index.data.validation.RegexPatterns
import app.index.data.validation.Validatable
import app.index.data.validation.Validations
import io.konform.validation.Validation
import io.konform.validation.constraints.maxLength
import io.konform.validation.constraints.minLength
import io.konform.validation.constraints.pattern
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Groups items in a list, for example, a list of movies to watch can have categories for the genre
 */
@Serializable
data class CategoryData(
    @Contextual val id: IxId<CategoryData>,
    @Contextual val user_id: IxId<UserData>,
    @Contextual val list_id: IxId<ListData>,
    var name: String,
    var color: String, // Represented as #010101 hex color
    val created_at: Long = DatetimeUtils.currentMillis(),
    val edited_at: Long? = null,
) {
    @Serializable
    data class CategoryCreateRequestData(
        val name: String,
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
        val name: String,
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
        val name: String,
        val color: String,
    )
}
