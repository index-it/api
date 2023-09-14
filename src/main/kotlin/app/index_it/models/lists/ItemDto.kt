package app.index_it.models.lists

import app.index_it.core.logic.currentMillis
import app.index_it.models.Validatable
import app.index_it.models.tasks.TaskDto
import app.index_it.models.user.UserDto
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

/**
 * Represents an item in a list
 */
@Serializable
data class ItemDto(
    @Contextual @SerialName("_id") val id: Id<ItemDto> = ObjectId().toId(),
    @Contextual val userId: Id<UserDto>,
    @Contextual val listId: Id<ListDto>,
    @Contextual val categoryId: Id<CategoryDto>,
    @Contextual val taskId: Id<TaskDto>? = null,
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
        @Contextual val categoryId: Id<CategoryDto>,
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
        @Contextual val categoryId: Id<CategoryDto>?,
        val name: String,
        val completed: Boolean
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
