package app.index_it.data.daos.task

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.tasks.TaskReminderJobDto
import app.index_it.data.sources.db.dbi.task.impl.TaskReminderJobDBIImpl

object TaskReminderJobDao {
    suspend fun create(jobId: IxId<TaskReminderJobDto>, taskId: IxId<TaskDto>) {
        TaskReminderJobDBIImpl.create(jobId, taskId)
    }

    suspend fun get(jobId: IxId<TaskReminderJobDto>): TaskReminderJobDto? {
        return TaskReminderJobDBIImpl.get(jobId)
    }

    suspend fun delete(jobId: IxId<TaskReminderJobDto>) {
        TaskReminderJobDBIImpl.delete(jobId)
    }

    suspend fun deleteAllOfTask(taskId: IxId<TaskDto>) {
        TaskReminderJobDBIImpl.deleteAllOfTask(taskId)
    }
}