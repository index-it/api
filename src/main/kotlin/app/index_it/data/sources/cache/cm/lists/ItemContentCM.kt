package app.index_it.data.sources.cache.cm.lists

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ItemContentDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.core.DoubleHashedCM

object ItemContentCM: DoubleHashedCM("item-contents") {
    fun get(userId: IxId<UserDto>, itemId: IxId<ItemDto>): ItemContentDto? =
        get(userId.toString(), itemId.toString())

    fun cache(userId: IxId<UserDto>, itemContentDto: ItemContentDto) {
        cache(userId.toString(), itemContentDto.id.toString(), itemContentDto)
    }

    fun delete(userId: IxId<UserDto>, itemId: IxId<ItemDto>) {
        delete(userId.toString(), itemId.toString())
    }

    fun deleteMultiple(userId: IxId<UserDto>, itemIds: List<IxId<ItemDto>>) {
        deleteMultiple(userId.toString(), *itemIds.map { it.toString() }.toTypedArray())
    }

    fun deleteAllOfUser(userId: IxId<UserDto>) {
        deleteAll("${userId}_*")
    }
}