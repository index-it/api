package app.index_it.data.sources.db.dbi.list

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.sources.db.dbi.DBI

interface ItemDBI : DBI {
    suspend fun exists(id: IxId<ItemDto>): Boolean
    suspend fun create(itemDto: ItemDto)
    suspend fun get(id: IxId<ItemDto>): ItemDto?
    suspend fun getOfCategory(id: IxId<CategoryDto>): List<ItemDto>
    suspend fun getOfList(id: IxId<ListDto>): List<ItemDto>
    suspend fun setCompletion(itemId: IxId<ItemDto>, completed: Boolean): ItemDto?
    suspend fun setLinking(itemId: IxId<ItemDto>, taskId: IxId<TaskDto>): ItemDto?
    suspend fun update(id: IxId<ItemDto>, itemUpdateRequestDto: ItemDto.ItemUpdateRequestDto): ItemDto?
    suspend fun delete(id: IxId<ItemDto>)
}