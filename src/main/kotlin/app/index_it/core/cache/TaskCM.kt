package app.index_it.core.cache

import app.index_it.models.tasks.TaskDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

object TaskCM: DoubleHashedCM("tasks") {
    fun getAll(userId: Id<UserDto>): List<TaskDto> = getAllValues(userId.toString())

    fun create(userId: Id<UserDto>, taskDto: TaskDto) {
        cacheValue(userId.toString(), taskDto.id.toString(), taskDto)
    }

    fun update(userId: Id<UserDto>, taskDto: TaskDto) {
        cacheValue(userId.toString(), taskDto.id.toString(), taskDto)
    }

    fun delete(userId: Id<UserDto>, taskId: Id<TaskDto>) {
        uncacheValue(userId.toString(), taskId.toString())
    }
}