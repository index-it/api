package app.index_it.data.sources.cache.cm.tasks

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto

interface TaskCM {
    fun getAll(userId: IxId<UserDto>): List<TaskDto>

    fun get(userId: IxId<UserDto>, taskId: IxId<TaskDto>): TaskDto?

    fun cacheAll(userId: IxId<UserDto>, tasksDto: List<TaskDto>)

    fun cache(userId: IxId<UserDto>, taskDto: TaskDto)

    fun update(userId: IxId<UserDto>, taskDto: TaskDto)

    fun delete(userId: IxId<UserDto>, taskId: IxId<TaskDto>)

    fun deleteAll(userId: IxId<UserDto>)
}