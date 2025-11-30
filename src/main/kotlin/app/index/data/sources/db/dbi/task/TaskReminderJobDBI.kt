package app.index.data.sources.db.dbi.task

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.tasks.TaskData
import app.index.data.models.tasks.TaskReminderJobData
import app.index.data.sources.db.dbi.DBI

interface TaskReminderJobDBI : DBI {
    suspend fun create(taskReminderJobCreateData: TaskReminderJobData.TaskReminderJobCreateData)

    suspend fun createAll(taskReminderJobCreateData: List<TaskReminderJobData.TaskReminderJobCreateData>)

    suspend fun get(jobId: IxId<TaskReminderJobData>): TaskReminderJobData?

    suspend fun getAllOfTask(taskId: IxId<TaskData>): List<TaskReminderJobData>

    suspend fun increaseRescheduleCount(id: IxId<TaskReminderJobData>): TaskReminderJobData?

    suspend fun delete(jobId: IxId<TaskReminderJobData>)

    suspend fun deleteMultiple(jobIds: List<IxId<TaskReminderJobData>>)

    suspend fun deleteAllOfTask(taskId: IxId<TaskData>)
}
