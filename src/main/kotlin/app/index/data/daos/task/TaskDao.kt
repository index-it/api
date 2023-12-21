package app.index.data.daos.task

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.newIxId
import app.index.data.models.tasks.TaskDto
import app.index.data.models.user.UserDto
import app.index.data.sources.cache.cm.tasks.TaskCM
import app.index.data.sources.db.dbi.task.TaskDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class TaskDao(
    private val taskDBI: TaskDBI,
    private val taskCM: TaskCM,
) {
    suspend fun create(
        userId: IxId<UserDto>,
        taskCreateRequestDto: TaskDto.TaskCreateRequestDto,
    ): TaskDto {
        val taskDto =
            TaskDto(
                id = newIxId(),
                userId = userId,
                itemId = taskCreateRequestDto.itemId,
                name = taskCreateRequestDto.name,
                description = taskCreateRequestDto.description,
                dueDate = taskCreateRequestDto.dueDate,
                rrule = taskCreateRequestDto.rrule,
                subTasks = taskCreateRequestDto.subTasks,
                completed = false,
                priority = taskCreateRequestDto.priority,
                createdAt = DatetimeUtils.currentMillis(),
                editedAt = null,
                completedAt = null,
            )
        taskDBI.create(taskDto)
        taskCM.cache(taskDto.userId, taskDto)

        return taskDto
    }

    suspend fun createNextOccurrence(
        task: TaskDto,
        dueDate: Long,
        rrule: String,
    ): TaskDto {
        val taskDto =
            TaskDto(
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
        taskDBI.create(taskDto)
        taskCM.cache(taskDto.userId, taskDto)

        return taskDto
    }

    suspend fun getAll(userId: IxId<UserDto>): List<TaskDto> {
        // TODO: Decide whether to fetch from cache or db in this case (probably fetch from db directly or not?)
        var tasks = taskCM.getAll(userId)

        if (tasks.isEmpty()) {
            tasks = taskDBI.get(userId)
            if (tasks.isNotEmpty()) {
                taskCM.cacheAll(userId, tasks)
            }
        }

        return tasks
    }

    suspend fun getAllUncompleted(userId: IxId<UserDto>) =
        getAll(userId)
            .filter { !it.completed }

    suspend fun getAllCompleted(userId: IxId<UserDto>) =
        getAll(userId)
            .filter { it.completed }

    suspend fun get(
        userId: IxId<UserDto>,
        taskId: IxId<TaskDto>,
    ): TaskDto? {
        var task = taskCM.get(userId, taskId)

        if (task == null) {
            task = taskDBI.get(userId, taskId)
                ?: return null
            taskCM.cache(userId, task)
        }

        return task
    }

    suspend fun setCompletion(
        userId: IxId<UserDto>,
        taskId: IxId<TaskDto>,
        completed: Boolean,
    ): TaskDto? {
        val updated = taskDBI.setCompletion(userId, taskId, completed)

        if (updated) {
            // This is done everywhere to make sure that cache is always in sync with the real data
            taskCM.delete(userId, taskId)
        }

        return get(userId, taskId)
    }

    suspend fun update(
        userId: IxId<UserDto>,
        taskId: IxId<TaskDto>,
        taskUpdateRequestDto: TaskDto.TaskUpdateRequestDto,
    ): TaskDto? {
        val updated = taskDBI.update(userId, taskId, taskUpdateRequestDto)

        if (updated) {
            taskCM.delete(userId, taskId)
        }

        return get(userId, taskId)
    }

    suspend fun delete(
        userId: IxId<UserDto>,
        taskId: IxId<TaskDto>,
    ) {
        taskDBI.delete(userId, taskId)
        taskCM.delete(userId, taskId)
    }
}
