package app.index_it.data.sources.cache.cm.lists

import app.index_it.data.models.lists.ItemContentDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.user.UserDto
import org.litote.kmongo.Id

object ItemContentCM: app.index_it.data.sources.cache.core.DoubleHashedCM("item-contents") {
    fun get(userId: Id<UserDto>, itemId: Id<ItemDto>): ItemContentDto? =
        get(userId.toString(), itemId.toString())

    fun cache(userId: Id<UserDto>, itemContentDto: ItemContentDto) {
        cache(userId.toString(), itemContentDto.id.toString(), itemContentDto)
    }

    fun delete(userId: Id<UserDto>, itemId: Id<ItemDto>) {
        delete(userId.toString(), itemId.toString())
    }

    fun deleteMultiple(userId: Id<UserDto>, itemIds: List<Id<ItemDto>>) {
        deleteMultiple(userId.toString(), *itemIds.map { it.toString() }.toTypedArray())
    }

    fun deleteAllOfUser(userId: Id<UserDto>) {
        deleteAll("${userId}_*")
    }
}