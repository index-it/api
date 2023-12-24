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
            user_id = userId,
            item_id = taskCreateRequestData.item_id,
            name = taskCreateRequestData.name,
            description = taskCreateRequestData.description,
            due_date = taskCreateRequestData.due_date,
            rrule = taskCreateRequestData.rrule,
            subtasks = taskCreateRequestData.subtasks,
            completed = false,
            priority = taskCreateRequestData.priority,
            created_at = DatetimeUtils.currentMillis(),
            edited_at = null,
            completed_at = null,
        )

        taskDBI.create(taskData)
        taskCM.cache(taskData.user_id, taskData)

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
                user_id = task.user_id,
                item_id = null, // Cannot have connected recurring tasks
                name = task.name,
                description = task.description,
                due_date = dueDate,
                rrule = rrule,
                subtasks = task.subtasks.map { it.apply { completed = false } },
                completed = false,
                created_at = DatetimeUtils.currentMillis(),
                edited_at = task.edited_at,
                completed_at = null,
            )
        taskDBI.create(taskData)
        taskCM.cache(taskData.user_id, taskData)

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
