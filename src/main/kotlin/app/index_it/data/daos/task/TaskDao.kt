package app.index_it.data.daos.task

import app.index_it.core.logic.currentMillis
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.core.logic.typedId.newIxId
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.tasks.TaskCM
import app.index_it.data.sources.db.dbi.task.impl.TaskDBIImpl

object TaskDao {
    suspend fun create(userId: IxId<UserDto>, taskCreateRequestDto: TaskDto.TaskCreateRequestDto): TaskDto {
        val taskDto = TaskDto(
            id = newIxId(),
            userId = userId,
            // listId = null,
            // categoryId = null,
            itemId = null,
            name = taskCreateRequestDto.name,
            description = taskCreateRequestDto.description,
            dueDate = taskCreateRequestDto.dueDate,
            subTasks = taskCreateRequestDto.subTasks,
            completed = false,
            priority = taskCreateRequestDto.priority,
            createdAt = currentMillis(),
            editedAt = null,
            completedAt = null,
        )
        TaskDBIImpl.create(taskDto)
        TaskCM.cache(taskDto.userId, taskDto)

        return taskDto
    }

    suspend fun createLinked(userId: IxId<UserDto>, item: ItemDto): TaskDto {
        val taskDto = TaskDto(
            id = newIxId(),
            userId = userId,
            // listId = item.listId,
            // categoryId = item.categoryId,
            itemId = item.id,
            name = item.name,
            description = null,
            dueDate = null,
            subTasks = mutableListOf(), // maybe enhance this in the future by scraping the item content and getting to-do lists from it
            completed = false, // handle this differently perhaps?
            createdAt = currentMillis(),
            editedAt = null,
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

        // This is done everywhere to make sure that cache is always in sync with the real data
        return if (updated) {
            TaskCM.delete(userId, taskId)
            get(userId, taskId)
        } else {
            null
        }
    }

    suspend fun setItemConnection(userId: IxId<UserDto>, taskId: IxId<TaskDto>, itemId: IxId<ItemDto>?): TaskDto? {
        val updated = TaskDBIImpl.setItemConnection(userId, taskId, itemId)

        return if (updated) {
            TaskCM.delete(userId, taskId)
            get(userId, taskId)
        } else {
            null
        }
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

        return if (updated) {
            TaskCM.delete(userId, taskId)
            get(userId, taskId)
        } else {
            null
        }
    }

    suspend fun delete(userId: IxId<UserDto>, taskId: IxId<TaskDto>) {
        TaskDBIImpl.delete(userId, taskId)
        TaskCM.delete(userId, taskId)
    }
}