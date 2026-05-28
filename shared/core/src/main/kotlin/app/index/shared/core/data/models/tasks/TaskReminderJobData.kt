package app.index.shared.core.data.models.tasks

import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.user.UserData
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * @property id job id
 * @property task
 * @property userId
 * @property scheduledAt
 * @property rescheduleCount how many times the job has been rescheduled because of the 30-day limit of Google Cloud Tasks
 */
@Serializable
data class TaskReminderJobData(
    @Contextual val id: IxId<TaskReminderJobData>,
    @Contextual val task: TaskData,
    @Contextual val userId: IxId<UserData>,
    val scheduledAt: Long,
    val rescheduleCount: Long
) {
    data class TaskReminderJobCreateData(
        val id: IxId<TaskReminderJobData>,
        val taskId: IxId<TaskData>,
        val userId: IxId<UserData>,
        val scheduledAt: Long,
    )
}