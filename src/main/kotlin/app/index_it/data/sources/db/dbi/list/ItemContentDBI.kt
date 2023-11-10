package app.index_it.data.sources.db.dbi.list

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ItemContentDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.sources.db.dbi.DBI

interface ItemContentDBI : DBI {
    suspend fun create(itemContentDto: ItemContentDto)
    suspend fun get(id: IxId<ItemDto>): ItemContentDto?
    suspend fun update(id: IxId<ItemDto>, itemContentCreateOrUpdateRequest: ItemContentDto.ItemContentCreateOrUpdateRequest): ItemContentDto?
    suspend fun delete(id: IxId<ItemDto>)
}