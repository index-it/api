package app.index_it.data.sources.db.dbi.task

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.tasks.TaskReminderJobDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.DBI

interface TaskReminderJobDBI : DBI {
    suspend fun create(jobId: IxId<TaskReminderJobDto>, taskId: IxId<TaskDto>, userId: IxId<UserDto>)

    suspend fun get(jobId: IxId<TaskReminderJobDto>): TaskReminderJobDto?

    suspend fun delete(jobId: IxId<TaskReminderJobDto>)

    suspend fun deleteAllOfTask(taskId: IxId<TaskDto>)
}