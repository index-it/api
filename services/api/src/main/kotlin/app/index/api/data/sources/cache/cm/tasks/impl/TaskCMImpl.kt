package app.index.api.data.sources.cache.cm.tasks.impl

import app.index.api.core.clients.RedisClient
import app.index.api.core.logic.ObjectMapper
import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.tasks.TaskData
import app.index.api.data.models.user.UserData
import app.index.api.data.sources.cache.cm.tasks.TaskCM
import app.index.api.data.sources.cache.core.DoubleHashedCM
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
    override fun getAll(userId: IxId<UserData>): List<TaskData> = getAll(userId.toString())

    override fun get(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
    ): TaskData? = get(userId.toString(), taskId.toString())

    override fun cacheAll(
        userId: IxId<UserData>,
        tasksDto: List<TaskData>,
    ) {
        cacheAll(userId.toString(), tasksDto.associateBy { it.id.toString() })
    }

    override fun cache(
        userId: IxId<UserData>,
        taskData: TaskData,
    ) {
        cache(userId.toString(), taskData.id.toString(), taskData)
    }

    override fun update(
        userId: IxId<UserData>,
        taskData: TaskData,
    ) {
        cache(userId.toString(), taskData.id.toString(), taskData)
    }

    override fun delete(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
    ) {
        delete(userId.toString(), taskId.toString())
    }

    override fun deleteAllOfUser(userId: IxId<UserData>) {
        deleteAll(userId.toString())
    }
}
