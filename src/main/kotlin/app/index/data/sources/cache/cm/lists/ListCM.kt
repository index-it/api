package app.index.data.sources.cache.cm.lists

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ListDto
import app.index.data.models.user.UserDto

interface ListCM {
    fun getAll(userId: IxId<UserDto>): List<ListDto>

    fun get(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
    ): ListDto?

    fun cacheAll(
        userId: IxId<UserDto>,
        listsDto: List<ListDto>,
    )

    fun cache(
        userId: IxId<UserDto>,
        listDto: ListDto,
    )

    fun update(
        userId: IxId<UserDto>,
        listDto: ListDto,
    )

    fun delete(
        userId: IxId<UserDto>,
        listId: IxId<ListDto>,
    )

    fun deleteAll(userId: IxId<UserDto>)
}
