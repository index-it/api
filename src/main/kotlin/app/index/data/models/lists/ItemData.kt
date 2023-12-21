package app.index.data.models.lists

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.data.validation.Validatable
import app.index.data.models.tasks.TaskData
import app.index.data.models.user.UserData
import app.index.data.validation.Validations
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents an item in a list
 */
@Serializable
data class ItemData(
    @Contextual val id: IxId<ItemData>,
    @Contextual val userId: IxId<UserData>,
    @Contextual val listId: IxId<ListData>,
    @Contextual val categoryId: IxId<CategoryData>,
    @Contextual val taskId: IxId<TaskData>? = null,
    val name: String,
    val completed: Boolean = false,
    @SerialName("created_at")
    val createdAt: Long = DatetimeUtils.currentMillis(),
    @SerialName("edited_at")
    val editedAt: Long? = null,
    @SerialName("completed_at")
    val completedAt: Long? = null,
) {
    @Serializable
    data class ItemCreateRequestData(
        @Contextual val categoryId: IxId<CategoryData>,
        val name: String,
    ) : Validatable<ItemCreateRequestData> {
        override fun validate() =
            Validation {
                ItemCreateRequestData::name {
                    minLength(Validations.Item.MIN_NAME_LENGTH)
                    maxLength(Validations.Item.MAX_NAME_LENGTH)
                }
            }.invoke(this)
    }

    @Serializable
    data class ItemUpdateRequestData(
        @Contextual val categoryId: IxId<CategoryData>,
        val name: String,
    ) : Validatable<ItemUpdateRequestData> {
        override fun validate() =
            Validation {
                ItemUpdateRequestData::name {
                    minLength(Validations.Item.MIN_NAME_LENGTH)
                    maxLength(Validations.Item.MAX_NAME_LENGTH)
                }
            }.invoke(this)
    }

    @Serializable
    data class ItemTemplateResponseData(
        val name: String,
    )
}
