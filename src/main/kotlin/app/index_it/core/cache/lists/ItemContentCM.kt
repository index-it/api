package app.index_it.core.cache.lists

import app.index_it.core.cache.core.DoubleHashedCM
import app.index_it.models.lists.ItemContentDto
import app.index_it.models.lists.ItemDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

object ItemContentCM: DoubleHashedCM("item-contents") {
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