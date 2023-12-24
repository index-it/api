package app.index.data.sources.cache.cm.tasks

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.tasks.TaskData
import app.index.data.models.user.UserData

interface TaskCM {
    fun getAll(userId: IxId<UserData>): List<TaskData>

    fun get(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
    ): TaskData?

    fun cacheAll(
        userId: IxId<UserData>,
        tasksDto: List<TaskData>,
    )

    fun cache(
        userId: IxId<UserData>,
        taskData: TaskData,
    )

    fun update(
        userId: IxId<UserData>,
        taskData: TaskData,
    )

    fun delete(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
    )

    fun deleteAllOfUser(userId: IxId<UserData>)
}
