package app.index.data.models.lists

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.tasks.TaskData
import app.index.data.models.user.UserData
import app.index.data.validation.Validatable
import app.index.data.validation.Validations
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Represents an item in a list
 */
@Serializable
data class ItemData(
    @field:Schema(required = true)
    @Contextual val id: IxId<ItemData>,
    @field:Schema(required = true)
    @Contextual val user_id: IxId<UserData>,
    @field:Schema(required = true)
    @Contextual val list_id: IxId<ListData>,
    @Contextual val category_id: IxId<CategoryData>?,
    @Deprecated("Now multiple tasks can be connected to the same item because of lists sharing")
    @Contextual val task_id: IxId<TaskData>? = null,
    @field:Schema(required = true)
    val name: String,
    @field:Schema(required = true)
    val completed: Boolean = false,
    val link: String? = null,
    val note: String? = null,
    @field:Schema(required = true)
    val created_at: Long = DatetimeUtils.currentMillis(),
    val edited_at: Long? = null,
    val completed_at: Long? = null,
) {
    @Serializable
    data class ItemCreateRequestData(
        @Contextual val category_id: IxId<CategoryData>? = null,
        @field:Schema(required = true)
        val name: String,
        val link: String? = null,
        val note: String? = null
    ) : Validatable<ItemCreateRequestData> {
        override fun validate() =
            Validation {
                ItemCreateRequestData::name {
                    minLength(Validations.Item.MIN_NAME_LENGTH)
                    maxLength(Validations.Item.MAX_NAME_LENGTH)
                }
                ItemCreateRequestData::link ifPresent {
                    maxLength(Validations.Item.MAX_LINK_LENGTH)
                }
                ItemCreateRequestData::note ifPresent {
                    maxLength(Validations.Item.MAX_NOTE_LENGTH)
                }
            }.invoke(this)
    }

    @Serializable
    data class ItemUpdateRequestData(
        @Contextual val category_id: IxId<CategoryData>? = null,
        @field:Schema(required = true)
        val name: String,
        val link: String? = null,
        val note: String? = null
    ) : Validatable<ItemUpdateRequestData> {
        override fun validate() =
            Validation {
                ItemUpdateRequestData::name {
                    minLength(Validations.Item.MIN_NAME_LENGTH)
                    maxLength(Validations.Item.MAX_NAME_LENGTH)
                }
                ItemUpdateRequestData::link ifPresent {
                    maxLength(Validations.Item.MAX_LINK_LENGTH)
                }
                ItemUpdateRequestData::note ifPresent {
                    maxLength(Validations.Item.MAX_NOTE_LENGTH)
                }
            }.invoke(this)
    }

    @Serializable
    data class ItemTemplateResponseData(
        @field:Schema(required = true)
        val name: String,
    )
}
