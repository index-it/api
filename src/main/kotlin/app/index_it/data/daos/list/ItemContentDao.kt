package app.index_it.daos.list

import app.index_it.core.cache.lists.ItemContentCM
import app.index_it.core.db.lists.ItemContentDBM
import app.index_it.models.lists.ItemContentDto
import app.index_it.models.lists.ItemDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

object ItemContentDao {
    fun create(userId: Id<UserDto>, itemId: Id<ItemDto>): ItemContentDto? {
        if (!ItemDao.exists(userId, itemId)) {
            return  null
        }

        val itemContentDto = ItemContentDto(
            userId = userId,
            itemId = itemId,
            content = ""
        )

        ItemContentDBM.create(itemContentDto)
        ItemContentCM.cache(userId, itemContentDto)

        return itemContentDto
    }

    fun get(userId: Id<UserDto>, itemId: Id<ItemDto>): ItemContentDto? {
        var content = ItemContentCM.get(userId, itemId)

        if (content == null) {
            content = ItemContentDBM.get(userId, itemId)
                ?: return null
            ItemContentCM.cache(userId, content)
        }

        return content
    }

    fun getOrCreate(userId: Id<UserDto>, itemId: Id<ItemDto>) =
        get(userId, itemId) ?: create(userId, itemId)

    fun update(userId: Id<UserDto>, itemId: Id<ItemDto>, itemContentCreateOrUpdateRequest: ItemContentDto.ItemContentCreateOrUpdateRequest): ItemContentDto? {
        val content = ItemContentDBM.update(userId, itemId, itemContentCreateOrUpdateRequest)

        if (content != null)
            ItemContentCM.cache(userId, content)
        else
            ItemContentCM.delete(userId, itemId)

        return content
    }

    fun delete(userId: Id<UserDto>, itemId: Id<ItemDto>) {
        ItemContentCM.delete(userId, itemId)
        ItemContentDBM.delete(userId, itemId)
    }

    fun deleteAllOfItems(userId: Id<UserDto>, itemIds: List<Id<ItemDto>>) {
        ItemContentCM.deleteMultiple(userId, itemIds)
        ItemContentDBM.deleteAllOfItems(userId, itemIds)
    }

    fun deleteAllOfUser(userId: Id<UserDto>) {
        ItemContentCM.deleteAllOfUser(userId)
        ItemContentDBM.deleteAllOfUser(userId)
    }
}