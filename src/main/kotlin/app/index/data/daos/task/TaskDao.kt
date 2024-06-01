package app.index.data.daos.task

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.newIxId
import app.index.data.models.lists.ItemData
import app.index.data.models.tasks.TaskData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.task.TaskDBI
import kotlinx.datetime.LocalDate
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class TaskDao(
    private val taskDBI: TaskDBI,
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
            reminders = taskCreateRequestData.reminders,
            completed = false,
            priority = taskCreateRequestData.priority,
            created_at = DatetimeUtils.currentMillis(),
            edited_at = null,
            completed_at = null,
        )

        taskDBI.create(taskData)

        return taskData
    }

    suspend fun createNextOccurrence(
        task: TaskData,
        dueDate: LocalDate,
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
                reminders = task.reminders,
                completed = false,
                priority = task.priority,
                created_at = DatetimeUtils.currentMillis(),
                edited_at = task.edited_at,
                completed_at = null,
            )
        taskDBI.create(taskData)

        return taskData
    }

    suspend fun getAll(userId: IxId<UserData>): List<TaskData> {
        return taskDBI.get(userId)
    }

    suspend fun getAllUncompleted(userId: IxId<UserData>): List<TaskData> {
        return taskDBI.getUncompleted(userId)
    }

    suspend fun getAllCompleted(userId: IxId<UserData>): List<TaskData> {
        return taskDBI.getCompleted(userId)
    }

    suspend fun getAllConnectedToItem(itemId: IxId<ItemData>): List<TaskData> {
        return taskDBI.getConnectedToItem(itemId)
    }

    suspend fun get(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
    ): TaskData? {
        return taskDBI.get(userId, taskId)
    }

    suspend fun setCompletionOfAllTasksConnectedToItem(
        itemId: IxId<ItemData>,
        completed: Boolean
    ): List<TaskData> {
        return taskDBI.setCompletionOfAllTasksConnectedToItem(itemId, completed)
    }

    suspend fun setCompletion(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
        completed: Boolean,
    ): TaskData? {
        return taskDBI.setCompletion(userId, taskId, completed)
    }

    suspend fun update(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
        taskUpdateRequestData: TaskData.TaskUpdateRequestData,
    ): TaskData? {
        taskDBI.update(userId, taskId, taskUpdateRequestData)

        return get(userId, taskId)
    }

    suspend fun delete(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
    ) : Boolean {
        return taskDBI.delete(userId, taskId)
    }
}
