package app.index.data.models.tasks

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserData
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * @property id job id
 * @property task
 * @property userId
 * @property scheduledAt
 */
@Serializable
data class TaskReminderJobData(
    @Contextual val id: IxId<TaskReminderJobData>,
    @Contextual val task: TaskData,
    @Contextual val userId: IxId<UserData>,
    val scheduledAt: Long
) {
    data class TaskReminderJobCreateData(
        val id: IxId<TaskReminderJobData>,
        val taskId: IxId<TaskData>,
        val userId: IxId<UserData>,
        val scheduledAt: Long
    )
}