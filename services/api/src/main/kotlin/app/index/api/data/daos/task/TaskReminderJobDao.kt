package app.index.api.data.daos.task

import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.tasks.TaskData
import app.index.shared.core.data.models.tasks.TaskReminderJobData
import app.index.api.data.sources.db.dbi.task.TaskReminderJobDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class TaskReminderJobDao(
    private val taskReminderJobDBI: TaskReminderJobDBI,
) {
    suspend fun create(
        taskReminderJobCreateData: TaskReminderJobData.TaskReminderJobCreateData
    ) {
        taskReminderJobDBI.create(taskReminderJobCreateData)
    }

    suspend fun createAll(
        taskReminderJobCreateData: List<TaskReminderJobData.TaskReminderJobCreateData>
    ) {
        taskReminderJobDBI.createAll(taskReminderJobCreateData)
    }

    suspend fun get(id: IxId<TaskReminderJobData>): TaskReminderJobData? {
        return taskReminderJobDBI.get(id)
    }

    suspend fun getAllOfTask(taskId: IxId<TaskData>): List<TaskReminderJobData> {
        return taskReminderJobDBI.getAllOfTask(taskId)
    }

    suspend fun increaseRescheduleCount(id: IxId<TaskReminderJobData>): TaskReminderJobData? {
        return taskReminderJobDBI.increaseRescheduleCount(id)
    }

    suspend fun delete(jobId: IxId<TaskReminderJobData>) {
        taskReminderJobDBI.delete(jobId)
    }

    suspend fun deleteMultiple(jobIds: List<IxId<TaskReminderJobData>>) {
        taskReminderJobDBI.deleteMultiple(jobIds)
    }

    suspend fun deleteAllOfTask(taskId: IxId<TaskData>) {
        taskReminderJobDBI.deleteAllOfTask(taskId)
    }
}
