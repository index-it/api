package app.index_it.core.db

import app.index_it.core.clients.MongoClient
import app.index_it.models.lists.ItemDto
import app.index_it.models.lists.ListDto
import app.index_it.models.user.UserDto
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import io.ktor.server.plugins.*
import org.litote.kmongo.*

object ItemDBM {
    private val col = MongoClient.database.getCollection<ItemDto>("items")

    init {
        col.ensureIndex(ItemDto::user_id)
        col.ensureIndex(ItemDto::list_id)
    }

    fun getAll(userId: Id<UserDto>, listId: Id<ListDto>): List<ItemDto> {
        return col.find(ItemDto::user_id eq userId, ItemDto::list_id eq listId).toList()
    }

    fun get(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>): ItemDto? {
        return col.findOne(ItemDto::user_id eq userId, ItemDto::list_id eq listId, ItemDto::id eq itemId)
    }

    fun create(itemDto: ItemDto) {
        col.save(itemDto)
    }

    fun update(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>, itemUpdateRequestDto: ItemDto.ItemUpdateRequestDto): ItemDto? {
        val properties: MutableList<SetTo<*>> = mutableListOf()

        if (itemUpdateRequestDto.name != null)
            properties.add(ItemDto::name setTo itemUpdateRequestDto.name)

        if (properties.isEmpty())
            throw BadRequestException("No values to update found in itemDto (id $itemId, listId $listId, userId $userId)")

        return col.findOneAndUpdate(
            and(ItemDto::id eq itemId, ItemDto::user_id eq userId, ItemDto::list_id eq listId),
            set(*properties.toTypedArray()),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>) {
        col.deleteOne(ItemDto::id eq itemId, ItemDto::user_id eq userId, ItemDto::list_id eq listId)
    }

    fun deleteAll(userId: Id<UserDto>) {
        col.deleteMany(ItemDto::user_id eq userId)
    }
}
