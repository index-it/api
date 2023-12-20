package app.index_it.data.daos.list

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.core.logic.typedId.newIxId
import app.index_it.data.models.lists.ItemContentDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.lists.ItemContentCM
import app.index_it.data.sources.cache.cm.lists.impl.ItemContentCMImpl
import app.index_it.data.sources.db.dbi.list.ItemContentDBI
import app.index_it.data.sources.db.dbi.list.impl.ItemContentDBIImpl
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ItemContentDao(
    private val itemDao: ItemDao,
    private val itemContentDBI: ItemContentDBI,
    private val itemContentCM: ItemContentCM
) {
    suspend fun create(userId: IxId<UserDto>, itemId: IxId<ItemDto>): ItemContentDto? {
        if (!itemDao.exists(userId, itemId)) {
            return null
        }

        val itemContentDto = ItemContentDto(
            id = newIxId(),
            userId = userId,
            itemId = itemId,
            content = ""
        )

        itemContentDBI.create(itemContentDto)
        itemContentCM.cache(userId, itemContentDto)

        return itemContentDto
    }

    suspend fun get(userId: IxId<UserDto>, itemId: IxId<ItemDto>): ItemContentDto? {
        var content = itemContentCM.get(userId, itemId)

        if (content == null) {
            content = itemContentDBI.get(userId, itemId)
                ?: return null
            itemContentCM.cache(userId, content)
        }

        return content
    }

    suspend fun getOrCreate(userId: IxId<UserDto>, itemId: IxId<ItemDto>) =
        get(userId, itemId) ?: create(userId, itemId)

    suspend fun update(userId: IxId<UserDto>, itemId: IxId<ItemDto>, itemContentCreateOrUpdateRequest: ItemContentDto.ItemContentCreateOrUpdateRequest): ItemContentDto? {
        val updated = itemContentDBI.update(userId, itemId, itemContentCreateOrUpdateRequest)

        if (updated) {
            itemContentCM.delete(userId, itemId)
        }

        return get(userId, itemId)
    }

    suspend fun delete(userId: IxId<UserDto>, itemId: IxId<ItemDto>) {
        itemContentCM.delete(userId, itemId)
        itemContentDBI.delete(userId, itemId)
    }
}