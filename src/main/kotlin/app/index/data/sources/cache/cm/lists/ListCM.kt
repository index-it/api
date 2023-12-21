package app.index.data.sources.cache.cm.lists

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ListData
import app.index.data.models.user.UserData

interface ListCM {
    fun getAll(userId: IxId<UserData>): List<ListData>

    fun get(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ): ListData?

    fun cacheAll(
        userId: IxId<UserData>,
        listsDto: List<ListData>,
    )

    fun cache(
        userId: IxId<UserData>,
        listData: ListData,
    )

    fun update(
        userId: IxId<UserData>,
        listData: ListData,
    )

    fun delete(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    )

    fun deleteAllOfUser(userId: IxId<UserData>)
}
