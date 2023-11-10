package app.index_it.data.daos.task

import app.index_it.core.logic.currentMillis
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.tasks.TaskCM
import app.index_it.data.sources.mongo.tasks.TaskDBM

object TaskDao {
    fun create(userId: IxId<UserDto>, taskCreateRequestDto: TaskDto.TaskCreateRequestDto): TaskDto {
        val taskDto = TaskDto(
            userId = userId,
            listId = null,
            categoryId = null,
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

    fun createLinked(userId: IxId<UserDto>, item: ItemDto): TaskDto {
        val taskDto = TaskDto(
            userId = userId,
            listId = item.listId,
            categoryId = item.categoryId,
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

    fun getAll(userId: IxId<UserDto>): List<TaskDto> {
        // TODO: Decide whether to fetch from cache or db in this case (probably fetch from db directly or not?)
        var tasks = TaskCM.getAll(userId)

        if (tasks.isEmpty()) {
            tasks = TaskDBM.getAll(userId)
            if (tasks.isNotEmpty())
                TaskCM.cacheAll(userId, tasks)
        }

        return tasks
    }

    fun getAllUncompleted(userId: IxId<UserDto>) =
        getAll(userId)
            .filter { !it.completed }

    fun getAllCompleted(userId: IxId<UserDto>) =
        getAll(userId)
            .filter { it.completed }

    fun get(userId: IxId<UserDto>, taskId: IxId<TaskDto>): TaskDto? {
        var task = TaskCM.get(userId, taskId)

        if (task == null) {
            task = TaskDBM.get(userId, taskId)
                ?: return null
            TaskCM.cache(userId, task)
        }

        return task
    }

    fun setCompletion(userId: IxId<UserDto>, taskId: IxId<TaskDto>, completed: Boolean): TaskDto? {
        val taskDto = TaskDBM.setCompletion(userId, taskId, completed)

        if (taskDto != null)
            TaskCM.update(userId, taskDto)
        else
            TaskCM.delete(userId, taskId)

        return taskDto
    }

    fun setLinking(userId: IxId<UserDto>, taskId: IxId<TaskDto>, listId: IxId<ListDto>?, categoryId: IxId<CategoryDto>?, itemId: IxId<ItemDto>?): TaskDto? {
        val taskDto = TaskDBM.setLinking(userId, taskId, listId, categoryId, itemId)

        if (taskDto != null)
            TaskCM.update(userId, taskDto)
        else
            TaskCM.delete(userId, taskId)

        return taskDto
    }
    
    fun setCategory(userId: IxId<UserDto>, taskId: IxId<TaskDto>, categoryId: IxId<CategoryDto>): TaskDto? {
        val taskDto = TaskDBM.setCategory(userId, taskId, categoryId)

        if (taskDto != null)
            TaskCM.update(userId, taskDto)
        else
            TaskCM.delete(userId, taskId)

        return taskDto
    }

    fun update(userId: IxId<UserDto>, taskId: IxId<TaskDto>, taskUpdateRequestDto: TaskDto.TaskUpdateRequestDto): TaskDto? {
        val taskDto = TaskDBM.update(userId, taskId, taskUpdateRequestDto)

        if (taskDto != null)
            TaskCM.update(userId, taskDto)
        else
            TaskCM.delete(userId, taskId)

        return taskDto
    }

    fun delete(userId: IxId<UserDto>, taskId: IxId<TaskDto>) {
        TaskDBM.delete(userId, taskId)
        TaskCM.delete(userId, taskId)
    }

    fun deleteAll(userId: IxId<UserDto>) {
        TaskDBM.deleteAllOfUser(userId)
        TaskCM.deleteAll(userId)
    }
}