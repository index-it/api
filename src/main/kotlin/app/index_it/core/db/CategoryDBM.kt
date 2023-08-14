package app.index_it.core.db

import app.index_it.core.clients.MongoClient
import app.index_it.models.lists.CategoryDto
import app.index_it.models.lists.ListDto
import app.index_it.models.user.UserDto
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import io.ktor.server.plugins.*
import org.litote.kmongo.*

object CategoryDBM {
    private val col = MongoClient.database.getCollection<ListDto>("lists")

    fun create(userId: Id<UserDto>, listId: Id<ListDto>, categoryDto: CategoryDto): ListDto? {
        return col.findOneAndUpdate(
            and(ListDto::id eq listId, ListDto::userId eq userId),
            push(ListDto::categories, categoryDto),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun getAll(userId: Id<UserDto>, listId: Id<ListDto>): MutableList<CategoryDto>? {
        return col.findOne(and(ListDto::id eq listId, ListDto::userId eq userId))?.categories
    }

    fun get(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>): CategoryDto? {
        return col.findOne(and(ListDto::id eq listId, ListDto::userId eq userId))?.categories?.firstOrNull { it.id == categoryId }
    }

    fun update(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>, categoryUpdateRequestDto: CategoryDto.CategoryUpdateRequestDto): ListDto? {
        val properties: MutableList<SetTo<*>> = mutableListOf()

        // TODO: Test this ^^
        if (categoryUpdateRequestDto.name != null)
            properties.add((ListDto::categories.posOp / CategoryDto::name) setTo categoryUpdateRequestDto.name)
        if (categoryUpdateRequestDto.color != null)
            properties.add((ListDto::categories.posOp / CategoryDto::color) setTo categoryUpdateRequestDto.color)

        if (properties.isEmpty())
            throw BadRequestException("No values to update found in categoryDto (id $categoryId, listId $listId, userId $userId)")

        return col.findOneAndUpdate(
            and(ListDto::id eq listId, ListDto::userId eq userId, (ListDto::categories / CategoryDto::id) eq categoryId),
            set(*properties.toTypedArray()),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>): ListDto? {
        return col.findOneAndUpdate(
            and(ListDto::id eq listId, ListDto::userId eq userId),
            pullByFilter(ListDto::categories, CategoryDto::id eq categoryId),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }
}
