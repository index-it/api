package app.index_it.data.daos.task

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.tasks.TaskReminderJobDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.task.TaskReminderJobDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class TaskReminderJobDao(
    private val taskReminderJobDBI: TaskReminderJobDBI
) {
    suspend fun create(jobId: IxId<TaskReminderJobDto>, taskId: IxId<TaskDto>, userId: IxId<UserDto>) {
        taskReminderJobDBI.create(jobId, taskId, userId)
    }

    suspend fun get(id: IxId<TaskReminderJobDto>): TaskReminderJobDto? {
        return taskReminderJobDBI.get(id)
    }

    suspend fun getFromTask(taskId: IxId<TaskDto>): TaskReminderJobDto? {
        return taskReminderJobDBI.getFromTask(taskId)
    }

    suspend fun delete(jobId: IxId<TaskReminderJobDto>) {
        taskReminderJobDBI.delete(jobId)
    }

    suspend fun deleteAllOfTask(taskId: IxId<TaskDto>) {
        taskReminderJobDBI.deleteAllOfTask(taskId)
    }
}