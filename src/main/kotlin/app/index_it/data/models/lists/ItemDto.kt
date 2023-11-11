package app.index_it.data.models.lists

import app.index_it.core.logic.currentMillis
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.Validatable
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
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
data class ItemDto(
    @Contextual @SerialName("_id") val id: IxId<ItemDto>,
    @Contextual val userId: IxId<UserDto>,
    @Contextual val listId: IxId<ListDto>,
    @Contextual val categoryId: IxId<CategoryDto>,
    @Contextual val taskId: IxId<TaskDto>? = null,
    val name: String,
    val completed: Boolean = false,
    @SerialName("created_at")
    val createdAt: Long = currentMillis(),
    @SerialName("edited_at")
    val editedAt: Long? = null,
    @SerialName("completed_at")
    val completedAt: Long? = null
) {
    @Serializable
    data class ItemCreateRequestDto(
        @Contextual val categoryId: IxId<CategoryDto>,
        val name: String
    ): Validatable<ItemCreateRequestDto> {
        override fun validate() = Validation {
            ItemCreateRequestDto::name {
                minLength(1)
                maxLength(100)
            }
        }.invoke(this)
    }

    @Serializable
    data class ItemUpdateRequestDto(
        @Contextual val categoryId: IxId<CategoryDto>,
        val name: String
    ): Validatable<ItemUpdateRequestDto> {
        override fun validate() = Validation {
            ItemUpdateRequestDto::name {
                minLength(1)
                maxLength(100)
            }
        }.invoke(this)
    }

    @Serializable
    data class ItemTemplateResponseDto(
        val name: String
    )
}
