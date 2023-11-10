package app.index_it.data.sources.cache.cm.lists

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.user.UserDto

object ListCM: app.index_it.data.sources.cache.core.DoubleHashedCM("lists") {
    fun getAll(userId: IxId<UserDto>): List<ListDto> = getAll(userId.toString())

    fun get(userId: IxId<UserDto>, listId: IxId<ListDto>): ListDto? = get(userId.toString(), listId.toString())

    fun cacheAll(userId: IxId<UserDto>, listsDto: List<ListDto>) {
        cacheAll(userId.toString(), listsDto.associateBy { it.id.toString() })
    }

    fun cache(userId: IxId<UserDto>, listDto: ListDto) {
        cache(userId.toString(), listDto.id.toString(), listDto)
    }

    fun update(userId: IxId<UserDto>, listDto: ListDto) {
        cache(userId.toString(), listDto.id.toString(), listDto)
    }

    fun delete(userId: IxId<UserDto>, listId: IxId<ListDto>) {
        delete(userId.toString(), listId.toString())
    }

    fun deleteAll(userId: IxId<UserDto>) {
        deleteAll(userId.toString())
    }
}
