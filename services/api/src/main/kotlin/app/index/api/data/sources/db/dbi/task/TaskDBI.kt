package app.index.api.data.sources.db.dbi.task

import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.lists.ItemData
import app.index.api.data.models.tasks.TaskData
import app.index.api.data.models.user.UserData
import app.index.api.data.sources.db.dbi.DBI

interface TaskDBI : DBI {
    suspend fun create(taskData: TaskData)

    suspend fun get(userId: IxId<UserData>): List<TaskData>

    suspend fun getUncompleted(userId: IxId<UserData>): List<TaskData>

    suspend fun getCompleted(userId: IxId<UserData>): List<TaskData>

    suspend fun getConnectedToItem(itemId: IxId<ItemData>): List<TaskData>

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
    ): TaskData?

    suspend fun setCompletionOfAllTasksConnectedToItem(
        itemId: IxId<ItemData>,
        completed: Boolean
    ): List<TaskData>

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
