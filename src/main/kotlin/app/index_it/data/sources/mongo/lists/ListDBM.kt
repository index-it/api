package app.index_it.data.sources.mongo.lists

import app.index_it.core.logic.currentMillis
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.mongo.MongoClient
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*

object ListDBM {
    private val col = MongoClient.database.getCollection<ListDto>("lists")

    init {
        col.ensureIndex(ListDto::userId)
    }

    fun getAll(userId: Id<UserDto>): List<ListDto> {
        return col.find(ListDto::userId eq userId).toList()
    }

    fun get(userId: Id<UserDto>, listId: Id<ListDto>): ListDto? {
        return col.findOne(ListDto::id eq listId, ListDto::userId eq userId)
    }

    fun create(listDto: ListDto) {
        col.save(listDto)
    }

    fun update(userId: Id<UserDto>, listId: Id<ListDto>, listUpdateRequestDto: ListDto.ListUpdateRequestDto): ListDto? {
        val properties: MutableList<SetTo<*>> = mutableListOf()

        properties.add(ListDto::name setTo listUpdateRequestDto.name)
        properties.add(ListDto::icon setTo listUpdateRequestDto.icon)
        properties.add(ListDto::color setTo listUpdateRequestDto.color)
        properties.add(ListDto::editedAt setTo currentMillis())

        return col.findOneAndUpdate(
            and(ListDto::id eq listId, ListDto::userId eq userId),
            set(*properties.toTypedArray()),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>) {
        col.deleteOne(Filters.and(ListDto::id eq listId, ListDto::userId eq userId))
    }

    fun deleteAll(userId: Id<UserDto>) {
        col.deleteMany(ListDto::userId eq userId)
    }
}
