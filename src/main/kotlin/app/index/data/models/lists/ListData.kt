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
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single list, which can contain categories to organize list items in it
 */
@Serializable
data class ListData(
    @Contextual val id: IxId<ListData>,
    @Contextual var user_id: IxId<UserData>,
    var name: String,
    var icon: String, // Single emoji at the moment
    var color: String, // Represented as #RRGGBB hex color
    val created_at: Long = DatetimeUtils.currentMillis(),
    val edited_at: Long? = null,
) {
    @Serializable
    data class ListCreateRequestData(
        var name: String,
        var icon: String,
        var color: String,
    ) : Validatable<ListCreateRequestData> {
        override fun validate() =
            Validation {
                ListCreateRequestData::name {
                    minLength(Validations.List.MIN_NAME_LENGTH)
                    maxLength(Validations.List.MAX_NAME_LENGTH)
                }
                ListCreateRequestData::color {
                    pattern(RegexPatterns.colorPattern)
                }
            }.invoke(this)
    }

    @Serializable
    data class ListUpdateRequestData(
        var name: String,
        var icon: String,
        var color: String,
    ) : Validatable<ListUpdateRequestData> {
        override fun validate() =
            Validation {
                ListUpdateRequestData::name {
                    minLength(Validations.List.MIN_NAME_LENGTH)
                    maxLength(Validations.List.MAX_NAME_LENGTH)
                }
                ListUpdateRequestData::color {
                    pattern(RegexPatterns.colorPattern)
                }
            }.invoke(this)
    }

    @Serializable
    data class ListTemplateResponseData(
        val name: String,
        val color: String,
    )
}
