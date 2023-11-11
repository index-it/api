package app.index_it.data.sources.db.dbi.list

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.DBI

interface ItemDBI : DBI {
    suspend fun exists(userId: IxId<UserDto>, itemId: IxId<ItemDto>): Boolean
    suspend fun create(itemDto: ItemDto)
    suspend fun get(userId: IxId<UserDto>, itemId: IxId<ItemDto>): ItemDto?
    suspend fun getOfCategory(userId: IxId<UserDto>, categoryId: IxId<CategoryDto>): List<ItemDto>
    suspend fun getOfList(userId: IxId<UserDto>, listId: IxId<ListDto>): List<ItemDto>
    suspend fun setCompletion(userId: IxId<UserDto>, itemId: IxId<ItemDto>, completed: Boolean): Boolean
    suspend fun setLinking(userId: IxId<UserDto>, itemId: IxId<ItemDto>, taskId: IxId<TaskDto>?): Boolean
    suspend fun update(userId: IxId<UserDto>, itemId: IxId<ItemDto>, itemUpdateRequestDto: ItemDto.ItemUpdateRequestDto): Boolean
    suspend fun delete(userId: IxId<UserDto>, itemId: IxId<ItemDto>)
}