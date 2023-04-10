package app.index_it.core.db

import app.index_it.core.clients.MongoClient
import app.index_it.models.lists.CategoryDto
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
        col.ensureIndex(ItemDto::userId)
        col.ensureIndex(ItemDto::listId)
    }

    fun getAll(userId: Id<UserDto>, listId: Id<ListDto>): List<ItemDto> {
        return col.find(ItemDto::userId eq userId, ItemDto::listId eq listId).toList()
    }

    fun get(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>): ItemDto? {
        return col.findOne(ItemDto::userId eq userId, ItemDto::listId eq listId, ItemDto::id eq itemId)
    }

    fun getAllOfCategory(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>): List<ItemDto> {
        return col.find(ItemDto::userId eq userId, ItemDto::listId eq listId, ItemDto::categoryId eq categoryId).toList()
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
            and(ItemDto::id eq itemId, ItemDto::userId eq userId, ItemDto::listId eq listId),
            set(*properties.toTypedArray()),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>) {
        col.deleteOne(ItemDto::id eq itemId, ItemDto::userId eq userId, ItemDto::listId eq listId)
    }

    fun deleteAllOfUser(userId: Id<UserDto>) {
        col.deleteMany(ItemDto::userId eq userId)
    }

    fun deleteAllOfList(userId: Id<UserDto>, listId: Id<ListDto>) {
        col.deleteMany(ItemDto::listId eq listId, ItemDto::userId eq userId)
    }

    fun deleteAllOfCategory(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>) {
        col.deleteMany(ItemDto::listId eq listId, ItemDto::userId eq userId, ItemDto::categoryId eq categoryId)
    }
}
