package app.index.shared.core.data.sources.db.dbi.task

import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.lists.ItemData
import app.index.shared.core.data.models.tasks.TaskData
import app.index.shared.core.data.models.user.UserData
import app.index.shared.core.data.sources.db.dbi.DBI

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
