package app.index.data.daos.task

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.newIxId
import app.index.data.models.tasks.TaskData
import app.index.data.models.user.UserData
import app.index.data.sources.cache.cm.tasks.TaskCM
import app.index.data.sources.db.dbi.task.TaskDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class TaskDao(
    private val taskDBI: TaskDBI,
    private val taskCM: TaskCM,
) {
    suspend fun create(
        userId: IxId<UserData>,
        taskCreateRequestData: TaskData.TaskCreateRequestData,
    ): TaskData {
        val taskData = TaskData(
            id = newIxId(),
            userId = userId,
            itemId = taskCreateRequestData.itemId,
            name = taskCreateRequestData.name,
            description = taskCreateRequestData.description,
            dueDate = taskCreateRequestData.dueDate,
            rrule = taskCreateRequestData.rrule,
            subTasks = taskCreateRequestData.subTasks,
            completed = false,
            priority = taskCreateRequestData.priority,
            createdAt = DatetimeUtils.currentMillis(),
            editedAt = null,
            completedAt = null,
        )

        taskDBI.create(taskData)
        taskCM.cache(taskData.userId, taskData)

        return taskData
    }

    suspend fun createNextOccurrence(
        task: TaskData,
        dueDate: Long,
        rrule: String,
    ): TaskData {
        val taskData =
            TaskData(
                id = newIxId(),
                userId = task.userId,
                itemId = null, // Cannot have connected recurring tasks
                name = task.name,
                description = task.description,
                dueDate = dueDate,
                rrule = rrule,
                subTasks = task.subTasks.map { it.apply { completed = false } },
                completed = false,
                createdAt = DatetimeUtils.currentMillis(),
                editedAt = task.editedAt,
                completedAt = null,
            )
        taskDBI.create(taskData)
        taskCM.cache(taskData.userId, taskData)

        return taskData
    }

    suspend fun getAll(userId: IxId<UserData>): List<TaskData> {
        var tasks = taskCM.getAll(userId)

        if (tasks.isEmpty()) {
            tasks = taskDBI.get(userId)
            if (tasks.isNotEmpty()) {
                taskCM.cacheAll(userId, tasks)
            }
        }

        return tasks
    }

    suspend fun getAllUncompleted(userId: IxId<UserData>) =
        getAll(userId)
            .filter { !it.completed }

    suspend fun getAllCompleted(userId: IxId<UserData>) =
        getAll(userId)
            .filter { it.completed }

    suspend fun get(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
    ): TaskData? {
        var task = taskCM.get(userId, taskId)

        if (task == null) {
            task = taskDBI.get(userId, taskId)
                ?: return null
            taskCM.cache(userId, task)
        }

        return task
    }

    suspend fun setCompletion(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
        completed: Boolean,
    ): TaskData? {
        val updated = taskDBI.setCompletion(userId, taskId, completed)

        if (updated) {
            // This is done everywhere to make sure that cache is always in sync with the real data
            taskCM.delete(userId, taskId)
        }

        return get(userId, taskId)
    }

    suspend fun update(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
        taskUpdateRequestData: TaskData.TaskUpdateRequestData,
    ): TaskData? {
        val updated = taskDBI.update(userId, taskId, taskUpdateRequestData)

        if (updated) {
            taskCM.delete(userId, taskId)
        }

        return get(userId, taskId)
    }

    suspend fun delete(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
    ) : Boolean {
        taskCM.delete(userId, taskId)
        return taskDBI.delete(userId, taskId)
    }
}
