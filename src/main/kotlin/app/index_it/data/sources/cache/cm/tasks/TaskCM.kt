package app.index_it.data.sources.cache.tasks

import app.index_it.data.sources.cache.core.DoubleHashedCM
import app.index_it.models.tasks.TaskDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

object TaskCM: app.index_it.data.sources.cache.core.DoubleHashedCM("tasks") {
    fun getAll(userId: Id<UserDto>): List<TaskDto> = getAll(userId.toString())

    fun get(userId: Id<UserDto>, taskId: Id<TaskDto>): TaskDto? = get(userId.toString(), taskId.toString())

    fun cacheAll(userId: Id<UserDto>, tasksDto: List<TaskDto>) {
        cacheAll(userId.toString(), tasksDto.associateBy { it.id.toString() })
    }

    fun cache(userId: Id<UserDto>, taskDto: TaskDto) {
        cache(userId.toString(), taskDto.id.toString(), taskDto)
    }

    fun update(userId: Id<UserDto>, taskDto: TaskDto) {
        cache(userId.toString(), taskDto.id.toString(), taskDto)
    }

    fun delete(userId: Id<UserDto>, taskId: Id<TaskDto>) {
        delete(userId.toString(), taskId.toString())
    }

    fun deleteAll(userId: Id<UserDto>) {
        deleteAll(userId.toString())
    }
}