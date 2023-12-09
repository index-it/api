package app.index_it.data.daos.task

import app.index_it.core.logic.DatetimeUtils
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.core.logic.typedId.newIxId
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.tasks.TaskCM
import app.index_it.data.sources.db.dbi.task.impl.TaskDBIImpl
import org.dmfs.rfc5545.recur.RecurrenceRule
import kotlin.math.max

object TaskDao {

    /**
     * If a task is recurring, this calculates the next occurrence date and the updated rrule in case `COUNT` was used as the end clause
     *
     * @return Null if this task isn't recurring or if it reached the end clause, a [Pair] with the next occurrence timestamp and updated rrule otherwise
     */
    fun calculateNextOccurrenceDueDateAndRRule(task: TaskDto): Pair<Long, String>? {
        if (task.dueDate == null || task.rrule == null)
            return null

        val rrule = RecurrenceRule(task.rrule)

        if (rrule.count != null) {
            rrule.count -= 1

            if (rrule.count < 1)
                return null
        }

        return rrule
            .iterator(max(task.dueDate, DatetimeUtils.currentMillis()), DatetimeUtils.utcTimeZone)
            .apply {
                try { skip(1) } catch (_: Exception) {}
            }
            .takeIf { it.hasNext() }
            ?.nextMillis()
            ?.let {
                Pair(it, rrule.toString())
            }
    }

    suspend fun create(userId: IxId<UserDto>, taskCreateRequestDto: TaskDto.TaskCreateRequestDto): TaskDto {
        val taskDto = TaskDto(
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
        TaskDBIImpl.create(taskDto)
        TaskCM.cache(taskDto.userId, taskDto)

        return taskDto
    }

    /*
    suspend fun createConnected(userId: IxId<UserDto>, item: ItemDto): TaskDto {
        val taskDto = TaskDto(
            id = newIxId(),
            userId = userId,
            itemId = item.id,
            name = item.name,
            description = null,
            dueDate = null,
            rrule = null,
            subTasks = mutableListOf(), // maybe enhance this in the future by scraping the item content and getting to-do lists from it
            completed = false, // handle this differently perhaps?
            createdAt = DatetimeUtils.currentMillis(),
            editedAt = null,
            completedAt = null,
        )
        TaskDBIImpl.create(taskDto)
        TaskCM.cache(taskDto.userId, taskDto)

        return taskDto
    }
     */

    suspend fun createNextOccurrence(task: TaskDto, dueDate: Long, rrule: String): TaskDto {
        val taskDto = TaskDto(
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
        TaskDBIImpl.create(taskDto)
        TaskCM.cache(taskDto.userId, taskDto)

        return taskDto
    }

    suspend fun getAll(userId: IxId<UserDto>): List<TaskDto> {
        // TODO: Decide whether to fetch from cache or db in this case (probably fetch from db directly or not?)
        var tasks = TaskCM.getAll(userId)

        if (tasks.isEmpty()) {
            tasks = TaskDBIImpl.get(userId)
            if (tasks.isNotEmpty())
                TaskCM.cacheAll(userId, tasks)
        }

        return tasks
    }

    suspend fun getAllUncompleted(userId: IxId<UserDto>) =
        getAll(userId)
            .filter { !it.completed }

    suspend fun getAllCompleted(userId: IxId<UserDto>) =
        getAll(userId)
            .filter { it.completed }

    suspend fun get(userId: IxId<UserDto>, taskId: IxId<TaskDto>): TaskDto? {
        var task = TaskCM.get(userId, taskId)

        if (task == null) {
            task = TaskDBIImpl.get(userId, taskId)
                ?: return null
            TaskCM.cache(userId, task)
        }

        return task
    }

    suspend fun setCompletion(userId: IxId<UserDto>, taskId: IxId<TaskDto>, completed: Boolean): TaskDto? {
        val updated = TaskDBIImpl.setCompletion(userId, taskId, completed)

        if (updated) {
            // This is done everywhere to make sure that cache is always in sync with the real data
            TaskCM.delete(userId, taskId)
        }

        return get(userId, taskId)
    }

    suspend fun setItemConnection(userId: IxId<UserDto>, taskId: IxId<TaskDto>, itemId: IxId<ItemDto>?): TaskDto? {
        val updated = TaskDBIImpl.setItemConnection(userId, taskId, itemId)

        if (updated) {
            TaskCM.delete(userId, taskId)
        }

        return get(userId, taskId)
    }

    /*
    fun setCategory(userId: IxId<UserDto>, taskId: IxId<TaskDto>, categoryId: IxId<CategoryDto>): TaskDto? {
        val taskDto = TaskDBM.setCategory(userId, taskId, categoryId)

        if (taskDto != null)
            TaskCM.update(userId, taskDto)
        else
            TaskCM.delete(userId, taskId)

        return taskDto
    }
     */

    suspend fun update(userId: IxId<UserDto>, taskId: IxId<TaskDto>, taskUpdateRequestDto: TaskDto.TaskUpdateRequestDto): TaskDto? {
        val updated = TaskDBIImpl.update(userId, taskId, taskUpdateRequestDto)

        if (updated) {
            TaskCM.delete(userId, taskId)
        }

        return get(userId, taskId)
    }

    suspend fun delete(userId: IxId<UserDto>, taskId: IxId<TaskDto>) {
        TaskDBIImpl.delete(userId, taskId)
        TaskCM.delete(userId, taskId)
    }
}