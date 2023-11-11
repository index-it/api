package app.index_it.data.sources.mongo.lists

import app.index_it.core.logic.currentMillis
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.mongo.MongoClient
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*

object ItemDBM {
    private val col = MongoClient.database.getCollection<ItemDto>("items")

    init {
        col.ensureIndex(ItemDto::userId)
        col.ensureIndex(ItemDto::listId)
    }

    fun exists(userId: IxId<UserDto>, itemId: IxId<ItemDto>): Boolean {
        return col.findOne(and(ItemDto::id eq itemId, ItemDto::userId eq  userId)) != null
    }

    fun getAll(userId: IxId<UserDto>, listId: IxId<ListDto>): List<ItemDto> {
        return col.find(ItemDto::userId eq userId, ItemDto::listId eq listId).toList()
    }

    fun get(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>): ItemDto? {
        return col.findOne(ItemDto::userId eq userId, ItemDto::listId eq listId, ItemDto::id eq itemId)
    }

    fun getAllOfCategory(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryId: IxId<CategoryDto>): List<ItemDto> {
        return col.find(ItemDto::userId eq userId, ItemDto::listId eq listId, ItemDto::categoryId eq categoryId).toList()
    }

    fun create(itemDto: ItemDto) {
        col.save(itemDto)
    }

    fun setCompletion(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>, completed: Boolean): ItemDto? {
        return col.findOneAndUpdate(
            and(ItemDto::id eq itemId, ItemDto::userId eq userId, ItemDto::listId eq listId),
            set(
                ItemDto::completed setTo completed,
                ItemDto::completedAt setTo if(completed) currentMillis() else null,
            ),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun setLinking(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>, taskId: IxId<TaskDto>?): ItemDto? {
        return col.findOneAndUpdate(
            and(ItemDto::id eq itemId, ItemDto::userId eq userId, ItemDto::listId eq listId),
            set(
                ItemDto::taskId setTo taskId
            ),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun update(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>, itemUpdateRequestDto: ItemDto.ItemUpdateRequestDto): ItemDto? {
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

    fun delete(userId: IxId<UserDto>, listId: IxId<ListDto>, itemId: IxId<ItemDto>) {
        col.deleteOne(ItemDto::id eq itemId, ItemDto::userId eq userId, ItemDto::listId eq listId)
    }

    fun deleteAllOfUser(userId: IxId<UserDto>) {
        col.deleteMany(ItemDto::userId eq userId)
    }

    fun deleteAllOfList(userId: IxId<UserDto>, listId: IxId<ListDto>) {
        col.deleteMany(ItemDto::listId eq listId, ItemDto::userId eq userId)
    }

    fun deleteAllOfCategory(userId: IxId<UserDto>, listId: IxId<ListDto>, categoryId: IxId<CategoryDto>) {
        col.deleteMany(ItemDto::listId eq listId, ItemDto::userId eq userId, ItemDto::categoryId eq categoryId)
    }
}
