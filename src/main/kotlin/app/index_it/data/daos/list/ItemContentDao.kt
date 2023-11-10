package app.index_it.data.daos.list

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ItemContentDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.lists.ItemContentCM
import app.index_it.data.sources.mongo.lists.ItemContentDBM

object ItemContentDao {
    fun create(userId: IxId<UserDto>, itemId: IxId<ItemDto>): ItemContentDto? {
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

    fun get(userId: IxId<UserDto>, itemId: IxId<ItemDto>): ItemContentDto? {
        var content = ItemContentCM.get(userId, itemId)

        if (content == null) {
            content = ItemContentDBM.get(userId, itemId)
                ?: return null
            ItemContentCM.cache(userId, content)
        }

        return content
    }

    fun getOrCreate(userId: IxId<UserDto>, itemId: IxId<ItemDto>) =
        get(userId, itemId) ?: create(userId, itemId)

    fun update(userId: IxId<UserDto>, itemId: IxId<ItemDto>, itemContentCreateOrUpdateRequest: ItemContentDto.ItemContentCreateOrUpdateRequest): ItemContentDto? {
        val content = ItemContentDBM.update(userId, itemId, itemContentCreateOrUpdateRequest)

        if (content != null)
            ItemContentCM.cache(userId, content)
        else
            ItemContentCM.delete(userId, itemId)

        return content
    }

    fun delete(userId: IxId<UserDto>, itemId: IxId<ItemDto>) {
        ItemContentCM.delete(userId, itemId)
        ItemContentDBM.delete(userId, itemId)
    }

    fun deleteAllOfItems(userId: IxId<UserDto>, itemIds: List<Id<ItemDto>>) {
        ItemContentCM.deleteMultiple(userId, itemIds)
        ItemContentDBM.deleteAllOfItems(userId, itemIds)
    }

    fun deleteAllOfUser(userId: IxId<UserDto>) {
        ItemContentCM.deleteAllOfUser(userId)
        ItemContentDBM.deleteAllOfUser(userId)
    }
}