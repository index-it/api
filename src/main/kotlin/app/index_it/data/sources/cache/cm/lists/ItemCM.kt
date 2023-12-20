package app.index_it.data.sources.cache.cm.lists

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.user.UserDto

interface ItemCM {
    fun getAll(userId: IxId<UserDto>, listId: IxId<ListDto>): List<ItemDto>

    fun get(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>): ItemDto?

    fun cacheAll(userId: IxId<UserDto>, listId: IxId<ListDto>, itemsDto: List<ItemDto>)

    fun cache(userId: IxId<UserDto>, listId: IxId<ListDto>, itemDto: ItemDto)

    fun delete(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>)

    fun deleteMultiple(userId: IxId<UserDto>, listId: IxId<ListDto>, itemIds: List<IxId<ItemDto>>)

    fun deleteAllOfUser(userId: IxId<UserDto>)

    fun deleteAllOfList(userId: IxId<UserDto>, listId: IxId<ListDto>)
}