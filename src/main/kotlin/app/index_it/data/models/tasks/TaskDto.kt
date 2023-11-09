package app.index_it.models.tasks

import app.index_it.core.logic.currentMillis
import app.index_it.models.Validatable
import app.index_it.models.lists.CategoryDto
import app.index_it.models.lists.ItemDto
import app.index_it.models.lists.ListDto
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
 * Represents a task, kind of like a TODO entry
 *
 * @param id
 * @param userId
 * @param itemId id of the item to which this task is linked - when the task gets completed, the item also gets completed and vice-versa
 * @param name
 * @param description
 * @param subTasks a list of sub tasks
 * @param priority task priority indicated as an int: 0 --> very low, 1 --> low, 2 --> medium, 3 --> high
 */
@Serializable
@Suppress("Unused")
data class TaskDto(
    @Contextual @SerialName("_id") val id: Id<TaskDto> = ObjectId().toId(),
    @Contextual val userId: Id<UserDto>,
    @Contextual val itemId: Id<ItemDto>? = null,
    @Contextual val categoryId: Id<CategoryDto>? = null,
    @Contextual val listId: Id<ListDto>? = null,
    val name: String,
    val description: String? = null,
    val subTasks: MutableList<SubTaskDto> = mutableListOf(),
    val dueDate: Long? = null,
    val completed: Boolean = false,
    val priority: Int? = null,
    @SerialName("created_at")
    val createdAt: Long = currentMillis(),
    @SerialName("edited_at")
    val editedAt: Long? = null,
    @SerialName("completed_at")
    val completedAt: Long? = null
) {
    @Serializable
    data class TaskCreateRequestDto(
        val name: String,
        val description: String? = null,
        val dueDate: Long? = null,
        val subTasks: MutableList<SubTaskDto> = mutableListOf(),
        val priority: Int? = null
    ): Validatable<TaskCreateRequestDto> {
        override fun validate() = Validation {
            TaskCreateRequestDto::name {
                minLength(1)
                maxLength(100)
            }
            TaskCreateRequestDto::description ifPresent {
                minLength(1)
                maxLength(500)
            }
        }.invoke(this)
    }

    @Serializable
    data class TaskUpdateRequestDto(
        val name: String,
        val description: String? = null,
        val dueDate: Long? = null,
        val subTasks: MutableList<SubTaskDto> = mutableListOf(),
        val priority: Int? = null,
    ): Validatable<TaskUpdateRequestDto> {
        override fun validate() = Validation {
            TaskUpdateRequestDto::name {
                minLength(1)
                maxLength(100)
            }
            TaskUpdateRequestDto::description ifPresent {
                minLength(1)
                maxLength(500)
            }
        }.invoke(this)
    }

    @Serializable
    data class TaskTemplateResponseDto(
        val name: String
    )
}

@Serializable
data class SubTaskDto(
    val name: String,
    val completed: Boolean
)
