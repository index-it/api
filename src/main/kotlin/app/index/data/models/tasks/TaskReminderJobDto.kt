package app.index.data.models.tasks

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserData
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * @property id job id
 * @property task
 */
@Serializable
data class TaskReminderJobDto(
    @Contextual val id: IxId<TaskReminderJobDto>,
    @Contextual val task: TaskData,
    @Contextual val userId: IxId<UserData>,
)
