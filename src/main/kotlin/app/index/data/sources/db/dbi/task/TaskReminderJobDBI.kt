package app.index.data.sources.db.dbi.task

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.tasks.TaskData
import app.index.data.models.tasks.TaskReminderJobDto
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.DBI

interface TaskReminderJobDBI : DBI {
    suspend fun create(
        jobId: IxId<TaskReminderJobDto>,
        taskId: IxId<TaskData>,
        userId: IxId<UserData>,
    )

    suspend fun get(jobId: IxId<TaskReminderJobDto>): TaskReminderJobDto?

    suspend fun getFromTask(taskId: IxId<TaskData>): TaskReminderJobDto?

    suspend fun delete(jobId: IxId<TaskReminderJobDto>)

    suspend fun deleteAllOfTask(taskId: IxId<TaskData>)
}
