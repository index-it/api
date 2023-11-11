package app.index_it.data.sources.db.dbi.list

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ItemContentDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.DBI

interface ItemContentDBI : DBI {
    suspend fun create(itemContentDto: ItemContentDto)
    suspend fun get(userId: IxId<UserDto>, itemId: IxId<ItemDto>): ItemContentDto?
    suspend fun update(userId: IxId<UserDto>, itemId: IxId<ItemDto>, itemContentCreateOrUpdateRequest: ItemContentDto.ItemContentCreateOrUpdateRequest): Boolean
    suspend fun delete(userId: IxId<UserDto>, itemId: IxId<ItemDto>)
}