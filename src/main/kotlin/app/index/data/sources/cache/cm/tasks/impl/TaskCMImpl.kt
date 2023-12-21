package app.index.data.sources.cache.cm.tasks.impl

import app.index.core.clients.RedisClient
import app.index.core.logic.ObjectMapper
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.tasks.TaskDto
import app.index.data.models.user.UserDto
import app.index.data.sources.cache.cm.tasks.TaskCM
import app.index.data.sources.cache.core.DoubleHashedCM
import org.koin.core.annotation.Single

@Single(createdAtStart = true, binds = [TaskCM::class])
class TaskCMImpl(
    redisClient: RedisClient,
    objectMapper: ObjectMapper,
) : TaskCM,
    DoubleHashedCM(
        keyBase = "tasks",
        redisClient,
        objectMapper,
    ) {
    override fun getAll(userId: IxId<UserDto>): List<TaskDto> = getAll(userId.toString())

    override fun get(
        userId: IxId<UserDto>,
        taskId: IxId<TaskDto>,
    ): TaskDto? = get(userId.toString(), taskId.toString())

    override fun cacheAll(
        userId: IxId<UserDto>,
        tasksDto: List<TaskDto>,
    ) {
        cacheAll(userId.toString(), tasksDto.associateBy { it.id.toString() })
    }

    override fun cache(
        userId: IxId<UserDto>,
        taskDto: TaskDto,
    ) {
        cache(userId.toString(), taskDto.id.toString(), taskDto)
    }

    override fun update(
        userId: IxId<UserDto>,
        taskDto: TaskDto,
    ) {
        cache(userId.toString(), taskDto.id.toString(), taskDto)
    }

    override fun delete(
        userId: IxId<UserDto>,
        taskId: IxId<TaskDto>,
    ) {
        delete(userId.toString(), taskId.toString())
    }

    override fun deleteAll(userId: IxId<UserDto>) {
        deleteAll(userId.toString())
    }
}
