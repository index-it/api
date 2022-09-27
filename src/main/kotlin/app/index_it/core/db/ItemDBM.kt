package app.index_it.core.db

import app.index_it.core.clients.MongoClient
import app.index_it.models.lists.ClientItemDto
import app.index_it.models.lists.ItemDto
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*

object ItemDBM {
    private val col = MongoClient.database.getCollection<ItemDto>("items")

    init {
        col.ensureUniqueIndex(ItemDto::user_id)
        col.ensureUniqueIndex(ItemDto::list_id)
    }

    fun getAll(userId: String, listId: String): List<ItemDto> {
        return col.find(ItemDto::user_id eq userId, ItemDto::list_id eq listId).toList()
    }

    fun create(itemDto: ItemDto) {
        col.save(itemDto)
    }

    fun update(userId: String, listId: String, itemId: String, clientItemDto: ClientItemDto): ItemDto? {
        return col.findOneAndUpdate(
            and(ItemDto::id eq itemId, ItemDto::user_id eq userId, ItemDto::list_id eq listId),
            set(
                ItemDto::name setTo clientItemDto.name,
                ItemDto::category_id setTo clientItemDto.category_id
            ),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun delete(userId: String, listId: String, itemId: String) {
        col.deleteOne(ItemDto::id eq itemId, ItemDto::user_id eq userId, ItemDto::list_id eq listId)
    }
}
