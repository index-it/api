package app.index.data.models.lists

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.Validatable
import app.index.data.models.tasks.TaskData
import app.index.data.models.user.UserData
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
                    minLength(1)
                    maxLength(100)
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
                    minLength(1)
                    maxLength(100)
                }
            }.invoke(this)
    }

    @Serializable
    data class ItemTemplateResponseData(
        val name: String,
    )
}
