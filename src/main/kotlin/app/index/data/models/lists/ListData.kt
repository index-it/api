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
 * Represents a single list, which can contain categories to organize list items in it
 */
@Serializable
data class ListData(
    @field:Schema(required = true)
    @Contextual val id: IxId<ListData>,
    @field:Schema(required = true)
    @Contextual var user_id: IxId<UserData>,
    @field:Schema(required = true)
    var name: String,
    @field:Schema(required = true)
    var icon: String, // Single emoji at the moment
    @field:Schema(required = true)
    var color: String, // Represented as #RRGGBB hex color
    @field:Schema(required = true)
    var public: Boolean,
    @field:Schema(required = true)
    val viewers: List<@Contextual IxId<UserData>>,
    @field:Schema(required = true)
    val editors: List<@Contextual IxId<UserData>>,
    @field:Schema(required = true)
    val created_at: Long = DatetimeUtils.currentMillis(),
    val edited_at: Long? = null,
) {
    /**
     * Returns a list of user ids that have access to this list
     */
    fun getUsersWithAccess() = viewers + editors + user_id

    @Serializable
    data class ListCreateRequestData(
        @field:Schema(required = true)
        var name: String,
        @field:Schema(required = true)
        var icon: String,
        @field:Schema(required = true)
        var color: String,
        @field:Schema(required = true)
        var public: Boolean = false,
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
        @field:Schema(required = true)
        var name: String,
        @field:Schema(required = true)
        var icon: String,
        @field:Schema(required = true)
        var color: String,
        @field:Schema(required = true)
        var public: Boolean = false,
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
    data class ListPermissionAddRequestData(
        @field:Schema(required = true)
        val email: String,
        @field:Schema(required = true)
        val editor: Boolean
    )

    @Serializable
    data class ListPermissionRemoveRequestData(
        @field:Schema(required = true)
        @Contextual val user_id: IxId<UserData>,
    )

    @Serializable
    data class ListTemplateResponseData(
        @field:Schema(required = true)
        val name: String,
        @field:Schema(required = true)
        val color: String,
    )

    @Serializable
    data class ListSingleUserAccessInfoResponseData(
        @field:Schema(required = true)
        @Contextual val user_id: IxId<UserData>,
        @field:Schema(required = true)
        val email: String,
        @field:Schema(required = true)
        val editor: Boolean,
    )
}
