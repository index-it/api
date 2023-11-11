package app.index_it.data.sources.db.dbi.list

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.DBI

interface ListDBI : DBI {
    suspend fun create(listDto: ListDto)
    suspend fun get(id: IxId<UserDto>): List<ListDto>
    suspend fun get(userId: IxId<UserDto>, listId: IxId<ListDto>): ListDto?
    suspend fun update(userId: IxId<UserDto>, listId: IxId<ListDto>, listUpdateRequestDto: ListDto.ListUpdateRequestDto): Boolean
    suspend fun delete(userId: IxId<UserDto>, listId: IxId<ListDto>)
}