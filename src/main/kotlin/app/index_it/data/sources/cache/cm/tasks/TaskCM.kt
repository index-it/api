package app.index_it.data.sources.cache.cm.tasks

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto

object TaskCM: app.index_it.data.sources.cache.core.DoubleHashedCM("tasks") {
    fun getAll(userId: IxId<UserDto>): List<TaskDto> = getAll(userId.toString())

    fun get(userId: IxId<UserDto>, taskId: IxId<TaskDto>): TaskDto? = get(userId.toString(), taskId.toString())

    fun cacheAll(userId: IxId<UserDto>, tasksDto: List<TaskDto>) {
        cacheAll(userId.toString(), tasksDto.associateBy { it.id.toString() })
    }

    fun cache(userId: IxId<UserDto>, taskDto: TaskDto) {
        cache(userId.toString(), taskDto.id.toString(), taskDto)
    }

    fun update(userId: IxId<UserDto>, taskDto: TaskDto) {
        cache(userId.toString(), taskDto.id.toString(), taskDto)
    }

    fun delete(userId: IxId<UserDto>, taskId: IxId<TaskDto>) {
        delete(userId.toString(), taskId.toString())
    }

    fun deleteAll(userId: IxId<UserDto>) {
        deleteAll(userId.toString())
    }
}