package app.index.data.sources.db.dbi.task

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.tasks.TaskData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.DBI

interface TaskDBI : DBI {
    suspend fun create(taskData: TaskData)

    suspend fun get(userId: IxId<UserData>): List<TaskData>

    suspend fun getUncompleted(userId: IxId<UserData>): List<TaskData>

    suspend fun getCompleted(userId: IxId<UserData>): List<TaskData>

    suspend fun get(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
    ): TaskData?

    suspend fun exists(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
    ): Boolean

    suspend fun setCompletion(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
        completed: Boolean,
    ): Boolean

    suspend fun update(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
        taskUpdateRequestData: TaskData.TaskUpdateRequestData,
    ): Boolean

    suspend fun delete(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
    ) : Boolean
}
