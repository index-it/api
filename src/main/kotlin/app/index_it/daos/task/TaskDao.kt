package app.index_it.daos.task

import app.index_it.core.cache.tasks.TaskCM
import app.index_it.core.db.tasks.TaskDBM
import app.index_it.core.logic.currentMillis
import app.index_it.models.lists.ItemDto
import app.index_it.models.lists.ListDto
import app.index_it.models.tasks.TaskDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

object TaskDao {
    fun create(userId: Id<UserDto>, taskCreateRequestDto: TaskDto.TaskCreateRequestDto): TaskDto {
        val taskDto = TaskDto(
            userId = userId,
            listId = null,
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
        TaskDBM.create(taskDto)
        TaskCM.cache(taskDto.userId, taskDto)

        return taskDto
    }

    fun createLinked(userId: Id<UserDto>, item: ItemDto): TaskDto {
        val taskDto = TaskDto(
            userId = userId,
            listId = item.listId,
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
        TaskDBM.create(taskDto)
        TaskCM.cache(taskDto.userId, taskDto)

        return taskDto
    }

    fun getAll(userId: Id<UserDto>): List<TaskDto> {
        // TODO: Decide whether to fetch from cache or db in this case (probably fetch from db directly or not?)
        var tasks = TaskCM.getAll(userId)

        if (tasks.isEmpty()) {
            tasks = TaskDBM.getAll(userId)
            if (tasks.isNotEmpty())
                TaskCM.cacheAll(userId, tasks)
        }

        return tasks
    }

    fun getAllUncompleted(userId: Id<UserDto>) =
        getAll(userId)
            .filter { !it.completed }

    fun getAllCompleted(userId: Id<UserDto>) =
        getAll(userId)
            .filter { it.completed }

    fun get(userId: Id<UserDto>, taskId: Id<TaskDto>): TaskDto? {
        var task = TaskCM.get(userId, taskId)

        if (task == null) {
            task = TaskDBM.get(userId, taskId)
                ?: return null
            TaskCM.cache(userId, task)
        }

        return task
    }

    fun setCompletion(userId: Id<UserDto>, taskId: Id<TaskDto>, completed: Boolean): TaskDto? {
        val taskDto = TaskDBM.setCompletion(userId, taskId, completed)

        if (taskDto != null)
            TaskCM.update(userId, taskDto)
        else
            TaskCM.delete(userId, taskId)

        return taskDto
    }

    fun setLinking(userId: Id<UserDto>, taskId: Id<TaskDto>, listId: Id<ListDto>?, itemId: Id<ItemDto>?): TaskDto? {
        val taskDto = TaskDBM.setLinking(userId, taskId, listId, itemId)

        if (taskDto != null)
            TaskCM.update(userId, taskDto)
        else
            TaskCM.delete(userId, taskId)

        return taskDto
    }

    fun update(userId: Id<UserDto>, taskId: Id<TaskDto>, taskUpdateRequestDto: TaskDto.TaskUpdateRequestDto): TaskDto? {
        val taskDto = TaskDBM.update(userId, taskId, taskUpdateRequestDto)

        if (taskDto != null)
            TaskCM.update(userId, taskDto)
        else
            TaskCM.delete(userId, taskId)

        return taskDto
    }

    fun delete(userId: Id<UserDto>, taskId: Id<TaskDto>) {
        TaskDBM.delete(userId, taskId)
        TaskCM.delete(userId, taskId)
    }

    fun deleteAll(userId: Id<UserDto>) {
        TaskDBM.deleteAllOfUser(userId)
        TaskCM.deleteAll(userId)
    }
}