package app.index_it.data.models.tasks

import app.index_it.core.logic.typedId.impl.IxId
import kotlinx.serialization.Contextual

/**
 * @property id job id
 * @property taskId
 */
data class TaskReminderJobDto(
    @Contextual val id: IxId<TaskReminderJobDto>,
    @Contextual val taskId: IxId<TaskDto>,
)
