package app.index_it.core.cache

import app.index_it.models.lists.ListDto

object ListCM: DoubleHashedCM("lists") {
    fun getAll(userId: String): List<ListDto> = getAllValues(userId)

    fun createAll(userId: String, listsDto: List<ListDto>) {
        cacheAllValues(userId, listsDto.associateBy { it.id })
    }

    fun create(userId: String, listDto: ListDto) {
        cacheValue(userId, listDto.id, listDto)
    }

    fun update(userId: String, listDto: ListDto) {
        cacheValue(userId, listDto.id, listDto)
    }

    fun delete(userId: String, listId: String) {
        uncacheValue(userId, listId)
    }
}
