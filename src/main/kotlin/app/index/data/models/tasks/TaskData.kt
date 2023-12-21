package app.index.data.models.tasks

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.data.validation.Validatable
import app.index.data.models.lists.ItemData
import app.index.data.models.user.UserData
import app.index.data.validation.Validations
import io.konform.validation.Validation
import io.konform.validation.jsonschema.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a task, kind of like a to-do
 *
 * @param id
 * @param userId
 * @param itemId id of the item to which this task is linked - when the task gets completed, the item also gets completed and vice-versa
 * @param name
 * @param description
 * @param subTasks a list of sub-tasks
 * @param dueDate Due date **UTC** timestamp
 * @param rrule Recurrence rule for the task
 * @param onDayReminder Day time, **UTC**, of the due date expressed in milliseconds representing when the reminder should be sent
 * @param completed
 * @param priority task priority indicated as an int: 0 --> very low, 1 --> low, 2 --> medium, 3 --> high
 * @param createdAt
 * @param editedAt
 * @param completedAt
 */
@Serializable
@Suppress("Unused")
data class TaskData(
    @Contextual val id: IxId<TaskData>,
    @Contextual val userId: IxId<UserData>,
    @Contextual val itemId: IxId<ItemData>? = null,
    val name: String,
    val description: String? = null,
    val subTasks: List<SubTaskData> = emptyList(),
    val dueDate: Long? = null,
    val rrule: String? = null,
    val onDayReminder: Long? = null,
    val completed: Boolean = false,
    val priority: Int? = null,
    @SerialName("created_at")
    val createdAt: Long = DatetimeUtils.currentMillis(),
    @SerialName("edited_at")
    val editedAt: Long? = null,
    @SerialName("completed_at")
    val completedAt: Long? = null,
) {
    @Serializable
    data class TaskCreateRequestData(
        val name: String,
        val description: String? = null,
        val dueDate: Long? = null,
        val rrule: String? = null,
        val onDayReminder: Long? = null,
        val subTasks: List<SubTaskData> = emptyList(),
        val priority: Int? = null,
        @Contextual val itemId: IxId<ItemData>? = null,
    ) : Validatable<TaskCreateRequestData> {
        override fun validate() =
            Validation {
                TaskCreateRequestData::name {
                    minLength(Validations.Task.MIN_NAME_LENGTH)
                    maxLength(Validations.Task.MAX_NAME_LENGTH)
                }
                TaskCreateRequestData::description ifPresent {
                    minLength(Validations.Task.MIN_DESCRIPTION_LENGTH)
                    maxLength(Validations.Task.MAX_DESCRIPTION_LENGTH)
                }
                TaskCreateRequestData::subTasks {
                    maxItems(Validations.Task.MAX_SUBTASK_COUNT)
                }
                TaskCreateRequestData::priority ifPresent {
                    minimum(Validations.Task.MINIMUM_PRIORITY)
                    maximum(Validations.Task.MAXIMUM_PRIORITY)
                }
            }.invoke(this)
    }

    @Serializable
    data class TaskUpdateRequestData(
        val name: String,
        val description: String? = null,
        val dueDate: Long? = null,
        val rrule: String? = null,
        val onDayReminder: Long? = null,
        val subTasks: List<SubTaskData> = emptyList(),
        val priority: Int? = null,
        @Contextual val itemId: IxId<ItemData>? = null,
    ) : Validatable<TaskUpdateRequestData> {
        override fun validate() =
            Validation {
                TaskUpdateRequestData::name {
                    minLength(Validations.Task.MIN_NAME_LENGTH)
                    maxLength(Validations.Task.MAX_NAME_LENGTH)
                }
                TaskUpdateRequestData::description ifPresent {
                    minLength(Validations.Task.MIN_DESCRIPTION_LENGTH)
                    maxLength(Validations.Task.MAX_DESCRIPTION_LENGTH)
                }
                TaskUpdateRequestData::subTasks {
                    maxItems(Validations.Task.MAX_SUBTASK_COUNT)
                }
                TaskUpdateRequestData::priority ifPresent {
                    minimum(Validations.Task.MINIMUM_PRIORITY)
                    maximum(Validations.Task.MAXIMUM_PRIORITY)
                }
            }.invoke(this)
    }

    @Serializable
    data class TaskTemplateResponseData(
        val name: String,
    )
}

@Serializable
data class SubTaskData(
    val name: String,
    var completed: Boolean,
)
