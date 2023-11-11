package app.index_it.data.sources.db.dbi.list.impl

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ItemContentDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.sources.db.dbi.list.ItemContentDBI

object ItemContentDBIImpl : ItemContentDBI {
    override suspend fun create(itemContentDto: ItemContentDto) {
        TODO("Not yet implemented")
    }

    override suspend fun get(id: IxId<ItemDto>): ItemContentDto? {
        TODO("Not yet implemented")
    }

    override suspend fun update(
        id: IxId<ItemDto>,
        itemContentCreateOrUpdateRequest: ItemContentDto.ItemContentCreateOrUpdateRequest
    ): ItemContentDto? {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: IxId<ItemDto>) {
        TODO("Not yet implemented")
    }
}