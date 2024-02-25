package app.index.data.sources.db.dbi.list

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ItemData
import app.index.data.models.lists.ListData
import app.index.data.models.tasks.TaskData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.DBI

interface ItemDBI : DBI {
    suspend fun exists(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ): Boolean

    suspend fun create(itemData: ItemData)

    suspend fun get(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ): ItemData?

    suspend fun getOfList(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ): List<ItemData>

    suspend fun getUncompletedOfList(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        ): List<ItemData>
    
    suspend fun getCompletedOfList(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        ): List<ItemData>

    suspend fun setCompletion(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
        completed: Boolean,
    ): Boolean

    suspend fun setTaskConnection(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
        taskId: IxId<TaskData>?,
    ): Boolean

    suspend fun update(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
        itemUpdateRequestData: ItemData.ItemUpdateRequestData,
    ): Boolean

    suspend fun delete(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ) : Boolean
}
