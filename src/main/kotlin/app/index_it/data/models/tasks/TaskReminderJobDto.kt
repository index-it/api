package app.index_it.data.models.tasks

import app.index_it.core.logic.typedId.impl.IxId
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * @property id job id
 * @property task
 */
@Serializable
data class TaskReminderJobDto(
    @Contextual val id: IxId<TaskReminderJobDto>,
    @Contextual val task: TaskDto,
)
