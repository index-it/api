package app.index_it.data.sources.db.lists

import app.index_it.core.clients.MongoClient
import app.index_it.core.logic.currentMillis
import app.index_it.models.lists.CategoryDto
import app.index_it.models.lists.ItemDto
import app.index_it.models.lists.ListDto
import app.index_it.models.tasks.TaskDto
import app.index_it.models.user.UserDto
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*

object ItemDBM {
    private val col = MongoClient.database.getCollection<ItemDto>("items")

    init {
        col.ensureIndex(ItemDto::userId)
        col.ensureIndex(ItemDto::listId)
    }

    fun exists(userId: Id<UserDto>, itemId: Id<ItemDto>): Boolean {
        return col.findOne(and(ItemDto::id eq itemId, ItemDto::userId eq  userId)) != null
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

    fun setCompletion(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>, completed: Boolean): ItemDto? {
        return col.findOneAndUpdate(
            and(ItemDto::id eq itemId, ItemDto::userId eq userId, ItemDto::listId eq listId),
            set(
                ItemDto::completed setTo completed,
                ItemDto::completedAt setTo if(completed) currentMillis() else null,
            ),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun setLinking(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>, taskId: Id<TaskDto>?): ItemDto? {
        return col.findOneAndUpdate(
            and(ItemDto::id eq itemId, ItemDto::userId eq userId, ItemDto::listId eq listId),
            set(
                ItemDto::taskId setTo taskId
            ),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun update(userId: Id<UserDto>, listId: Id<ListDto>, itemId: Id<ItemDto>, itemUpdateRequestDto: ItemDto.ItemUpdateRequestDto): ItemDto? {
        val properties: MutableList<SetTo<Any?>> = mutableListOf()

        properties.add(ItemDto::name setTo itemUpdateRequestDto.name)
        properties.add(ItemDto::categoryId setTo itemUpdateRequestDto.categoryId)
        properties.add(ItemDto::editedAt setTo currentMillis())

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
