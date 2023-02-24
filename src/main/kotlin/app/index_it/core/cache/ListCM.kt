package app.index_it.core.cache

import app.index_it.models.lists.ListDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

object ListCM: DoubleHashedCM("lists") {
    fun getAll(userId: Id<UserDto>): List<ListDto> = getAllValues(userId.toString())

    fun get(userId: Id<UserDto>, listId: Id<ListDto>): ListDto? = getValue(userId.toString(), listId.toString())

    fun createAll(userId: Id<UserDto>, listsDto: List<ListDto>) {
        cacheAllValues(userId.toString(), listsDto.associateBy { it.id.toString() })
    }

    fun create(userId: Id<UserDto>, listDto: ListDto) {
        cacheValue(userId.toString(), listDto.id.toString(), listDto)
    }

    fun update(userId: Id<UserDto>, listDto: ListDto) {
        cacheValue(userId.toString(), listDto.id.toString(), listDto)
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>) {
        uncacheValue(userId.toString(), listId.toString())
    }
}
