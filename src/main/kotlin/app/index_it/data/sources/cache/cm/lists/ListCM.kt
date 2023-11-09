package app.index_it.data.sources.cache.lists

import app.index_it.data.sources.cache.core.DoubleHashedCM
import app.index_it.models.lists.ListDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

object ListCM: app.index_it.data.sources.cache.core.DoubleHashedCM("lists") {
    fun getAll(userId: Id<UserDto>): List<ListDto> = getAll(userId.toString())

    fun get(userId: Id<UserDto>, listId: Id<ListDto>): ListDto? = get(userId.toString(), listId.toString())

    fun cacheAll(userId: Id<UserDto>, listsDto: List<ListDto>) {
        cacheAll(userId.toString(), listsDto.associateBy { it.id.toString() })
    }

    fun cache(userId: Id<UserDto>, listDto: ListDto) {
        cache(userId.toString(), listDto.id.toString(), listDto)
    }

    fun update(userId: Id<UserDto>, listDto: ListDto) {
        cache(userId.toString(), listDto.id.toString(), listDto)
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>) {
        delete(userId.toString(), listId.toString())
    }

    fun deleteAll(userId: Id<UserDto>) {
        deleteAll(userId.toString())
    }
}
