package app.index_it.data.daos.list

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.core.logic.typedId.newIxId
import app.index_it.data.models.lists.ItemContentDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.lists.ItemContentCM
import app.index_it.data.sources.db.dbi.list.impl.ItemContentDBIImpl

object ItemContentDao {
    suspend fun create(userId: IxId<UserDto>, itemId: IxId<ItemDto>): ItemContentDto? {
        if (!ItemDao.exists(userId, itemId)) {
            return  null
        }

        val itemContentDto = ItemContentDto(
            id = newIxId(),
            userId = userId,
            itemId = itemId,
            content = ""
        )

        ItemContentDBIImpl.create(itemContentDto)
        ItemContentCM.cache(userId, itemContentDto)

        return itemContentDto
    }

    suspend fun get(userId: IxId<UserDto>, itemId: IxId<ItemDto>): ItemContentDto? {
        var content = ItemContentCM.get(userId, itemId)

        if (content == null) {
            content = ItemContentDBIImpl.get(userId, itemId)
                ?: return null
            ItemContentCM.cache(userId, content)
        }

        return content
    }

    suspend fun getOrCreate(userId: IxId<UserDto>, itemId: IxId<ItemDto>) =
        get(userId, itemId) ?: create(userId, itemId)

    suspend fun update(userId: IxId<UserDto>, itemId: IxId<ItemDto>, itemContentCreateOrUpdateRequest: ItemContentDto.ItemContentCreateOrUpdateRequest): ItemContentDto? {
        val updated = ItemContentDBIImpl.update(userId, itemId, itemContentCreateOrUpdateRequest)

        if (updated) {
            ItemContentCM.delete(userId, itemId)
        }

        return get(userId, itemId)
    }

    suspend fun delete(userId: IxId<UserDto>, itemId: IxId<ItemDto>) {
        ItemContentCM.delete(userId, itemId)
        ItemContentDBIImpl.delete(userId, itemId)
    }

    /*
    fun deleteAllOfItems(userId: IxId<UserDto>, itemIds: List<Id<ItemDto>>) {
        ItemContentCM.deleteMultiple(userId, itemIds)
        ItemContentDBIImpl.deleteAllOfItems(userId, itemIds)
    }

    fun deleteAllOfUser(userId: IxId<UserDto>) {
        ItemContentCM.deleteAllOfUser(userId)
        ItemContentDBM.deleteAllOfUser(userId)
    }
     */
}