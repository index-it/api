package app.index_it.data.sources.db.lists

import app.index_it.core.clients.MongoClient
import app.index_it.models.lists.CategoryDto
import app.index_it.models.lists.ItemDto
import app.index_it.models.lists.ListDto
import app.index_it.models.user.UserDto
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*

object CategoryDBM {
    private val col = MongoClient.database.getCollection<CategoryDto>("categories")

    init {
        col.ensureIndex(CategoryDto::userId)
        col.ensureIndex(CategoryDto::listId)
    }

    fun create(categoryDto: CategoryDto) {
        return col.save(categoryDto)
    }

    fun getAll(userId: Id<UserDto>, listId: Id<ListDto>): List<CategoryDto> {
        return col.find(and(CategoryDto::userId eq userId, CategoryDto::listId eq listId)).toList()
    }

    fun get(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>): CategoryDto? {
        return col.findOne(CategoryDto::userId eq userId, CategoryDto::listId eq listId, CategoryDto::id eq categoryId)
    }

    fun update(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>, categoryUpdateRequestDto: CategoryDto.CategoryUpdateRequestDto): CategoryDto? {
        val properties: MutableList<SetTo<*>> = mutableListOf()

        properties.add(CategoryDto::name setTo categoryUpdateRequestDto.name)
        properties.add(CategoryDto::color setTo categoryUpdateRequestDto.color)

        return col.findOneAndUpdate(
            and(CategoryDto::id eq categoryId, CategoryDto::userId eq userId, CategoryDto::listId eq listId),
            set(*properties.toTypedArray()),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>) {
        col.deleteOne(CategoryDto::id eq categoryId, ItemDto::userId eq userId, ItemDto::listId eq listId)
    }

    fun deleteAllOfUser(userId: Id<UserDto>) {
        col.deleteMany(CategoryDto::userId eq userId)
    }

    fun deleteAllOfList(userId: Id<UserDto>, listId: Id<ListDto>) {
        col.deleteMany(CategoryDto::listId eq listId, CategoryDto::userId eq userId)
    }
}
