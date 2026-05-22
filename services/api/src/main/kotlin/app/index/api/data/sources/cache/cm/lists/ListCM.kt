package app.index.api.data.sources.cache.cm.lists

import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.lists.ListData
import app.index.api.data.models.user.UserData

@Deprecated("Caching in front of database entities is not recommended anymore")
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
