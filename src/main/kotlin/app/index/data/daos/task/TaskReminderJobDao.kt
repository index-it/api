package app.index.data.daos.task

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.tasks.TaskData
import app.index.data.models.tasks.TaskReminderJobDto
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.task.TaskReminderJobDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class TaskReminderJobDao(
    private val taskReminderJobDBI: TaskReminderJobDBI,
) {
    suspend fun create(
        jobId: IxId<TaskReminderJobDto>,
        taskId: IxId<TaskData>,
        userId: IxId<UserData>,
    ) {
        taskReminderJobDBI.create(jobId, taskId, userId)
    }

    suspend fun get(id: IxId<TaskReminderJobDto>): TaskReminderJobDto? {
        return taskReminderJobDBI.get(id)
    }

    suspend fun getOfTask(taskId: IxId<TaskData>): TaskReminderJobDto? {
        return taskReminderJobDBI.getFromTask(taskId)
    }

    suspend fun delete(jobId: IxId<TaskReminderJobDto>) {
        taskReminderJobDBI.delete(jobId)
    }

    suspend fun deleteAllOfTask(taskId: IxId<TaskData>) {
        taskReminderJobDBI.deleteAllOfTask(taskId)
    }
}
