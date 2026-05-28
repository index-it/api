package app.index.shared.core.data.models.tasks

import app.index.shared.core.logic.DatetimeUtils
import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.lists.ItemData
import app.index.shared.core.data.models.user.UserData
import app.index.shared.core.data.validation.Validatable
import app.index.shared.core.data.validation.Validations
import io.konform.validation.Invalid
import io.konform.validation.Validation
import io.konform.validation.ValidationError
import io.konform.validation.ValidationResult
import io.konform.validation.constraints.*
import io.konform.validation.path.PathSegment
import io.konform.validation.path.ValidationPath
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a task, kind of like a to-do
 *
 * @param id
 * @param user_id
 * @param item_id id of the item to which this task is linked - when the task gets completed, the item also gets completed and vice-versa
 * @param name
 * @param description
 * @param subtasks a list of sub-tasks
 * @param due_date Due date **UTC** timestamp
 * @param rrule Recurrence rule for the task
 * @param reminders
 * @param completed
 * @param priority task priority indicated as an int: 0 --> very low, 1 --> low, 2 --> medium, 3 --> high
 * @param created_at
 * @param edited_at
 * @param completed_at
 */
@Serializable
@Suppress("Unused")
data class TaskData(
    @Contextual val id: IxId<TaskData>,
    @Contextual val user_id: IxId<UserData>,
    @Contextual val item_id: IxId<ItemData>? = null,
    val name: String,
    val description: String? = null,
    val subtasks: List<SubTaskData> = emptyList(),
    val due_date: LocalDate? = null,
    val rrule: String? = null,
    val completed: Boolean = false,
    val priority: Int? = null,
    val reminders: List<TaskReminderData> = emptyList(),
    @SerialName("created_at")
    val created_at: Long = DatetimeUtils.currentMillis(),
    @SerialName("edited_at")
    val edited_at: Long? = null,
    @SerialName("completed_at")
    val completed_at: Long? = null,
) {
    @Serializable
    data class TaskCreateRequestData(
        val name: String,
        val description: String? = null,
        val due_date: LocalDate? = null,
        val rrule: String? = null,
        val reminders: List<TaskReminderData> = emptyList(),
        val subtasks: List<SubTaskData> = emptyList(),
        val priority: Int? = null,
        @Contextual val item_id: IxId<ItemData>? = null,
    ) : Validatable<TaskCreateRequestData> {
        override fun validate(): ValidationResult<TaskCreateRequestData> {
            if (reminders.isNotEmpty() && due_date == null) {
                return Invalid(
                    errors = listOf(ValidationError(
                        path=ValidationPath(listOf(PathSegment.toPathSegment(TaskCreateRequestData::reminders))),
                        message="cannot have reminders without a due date"
                    ))
                )
            }

            return Validation {
                TaskCreateRequestData::name {
                    minLength(Validations.Task.MIN_NAME_LENGTH)
                    maxLength(Validations.Task.MAX_NAME_LENGTH)
                }
                TaskCreateRequestData::description ifPresent {
                    minLength(Validations.Task.MIN_DESCRIPTION_LENGTH)
                    maxLength(Validations.Task.MAX_DESCRIPTION_LENGTH)
                }
                TaskCreateRequestData::subtasks {
                    maxItems(Validations.Task.MAX_SUBTASK_COUNT)
                }
                TaskCreateRequestData::subtasks onEach {
                    SubTaskData::name {
                        maxLength(Validations.Task.MAX_SUBTASK_NAME_LENGTH)
                    }
                }
                TaskCreateRequestData::priority ifPresent {
                    minimum(Validations.Task.MINIMUM_PRIORITY)
                    maximum(Validations.Task.MAXIMUM_PRIORITY)
                }
                TaskCreateRequestData::reminders {
                    maxItems(Validations.Task.MAX_REMINDERS_COUNT)
                }
                TaskCreateRequestData::reminders onEach {
                    TaskReminderData::days_before {
                        minimum(0)
                    }
                }
            }.invoke(this)
        }
    }

    @Serializable
    data class TaskUpdateRequestData(
        val name: String,
        val description: String? = null,
        val due_date: LocalDate? = null,
        val rrule: String? = null,
        val reminders: List<TaskReminderData> = emptyList(),
        val subtasks: List<SubTaskData> = emptyList(),
        val priority: Int? = null,
        @Contextual val item_id: IxId<ItemData>? = null,
    ) : Validatable<TaskUpdateRequestData> {
        override fun validate(): ValidationResult<TaskUpdateRequestData> {
            if (reminders.isNotEmpty() && due_date == null) {
                return Invalid(
                    errors = listOf(ValidationError(
                        path=ValidationPath(listOf(PathSegment.toPathSegment(TaskCreateRequestData::reminders))),
                        message="cannot have reminders without a due date"
                    ))
                )
            }

            return Validation {
                TaskUpdateRequestData::name {
                    minLength(Validations.Task.MIN_NAME_LENGTH)
                    maxLength(Validations.Task.MAX_NAME_LENGTH)
                }
                TaskUpdateRequestData::description ifPresent {
                    minLength(Validations.Task.MIN_DESCRIPTION_LENGTH)
                    maxLength(Validations.Task.MAX_DESCRIPTION_LENGTH)
                }
                TaskUpdateRequestData::subtasks {
                    maxItems(Validations.Task.MAX_SUBTASK_COUNT)
                }
                TaskUpdateRequestData::subtasks onEach {
                    SubTaskData::name {
                        maxLength(Validations.Task.MAX_SUBTASK_NAME_LENGTH)
                    }
                }
                TaskUpdateRequestData::priority ifPresent {
                    minimum(Validations.Task.MINIMUM_PRIORITY)
                    maximum(Validations.Task.MAXIMUM_PRIORITY)
                }
                TaskUpdateRequestData::reminders {
                    maxItems(Validations.Task.MAX_REMINDERS_COUNT)
                }
                TaskUpdateRequestData::reminders onEach {
                    TaskReminderData::days_before {
                        minimum(0)
                    }
                }
            }.invoke(this)
        }
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

/**
 * @param days_before days before the due date when the reminder should be triggered
 * @param time_offset time offset in milliseconds from the due date
 *
 * [time_offset] is in UTC, because of that it might be negative
 *
 * Example: I set a reminder with 0 [days_before] and at 01:00 AM but my timezone is UTC+4,
 * so the [time_offset] will be 01 - 4 = -3 hours => -3 * 60 * 60 * 1000 milliseconds
 */
@Serializable
data class TaskReminderData(
    val days_before: Int,
    val time_offset: Long
)
