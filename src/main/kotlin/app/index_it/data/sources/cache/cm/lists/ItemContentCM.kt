package app.index_it.data.sources.cache.cm.lists

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ItemContentDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.user.UserDto

interface ItemContentCM {
    fun get(userId: IxId<UserDto>, itemId: IxId<ItemDto>): ItemContentDto?

    fun cache(userId: IxId<UserDto>, itemContentDto: ItemContentDto)

    fun delete(userId: IxId<UserDto>, itemId: IxId<ItemDto>)

    fun deleteMultiple(userId: IxId<UserDto>, itemIds: List<IxId<ItemDto>>)

    fun deleteAllOfUser(userId: IxId<UserDto>)
}