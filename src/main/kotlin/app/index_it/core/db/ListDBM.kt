package app.index_it.core.db

import app.index_it.core.clients.MongoClient
import app.index_it.models.lists.CategoryDto
import app.index_it.models.lists.ClientCategoryDto
import app.index_it.models.lists.ClientListDto
import app.index_it.models.lists.ListDto
import app.index_it.models.user.UserDto
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*

object ListDBM {
    private val col = MongoClient.database.getCollection<ListDto>("lists")

    init {
        col.ensureUniqueIndex(ListDto::user_id)
    }

    object CategoryDBM {
        fun create(userId: Id<UserDto>, listId: Id<ListDto>, categoryDto: CategoryDto): ListDto? {
            return col.findOneAndUpdate(
                and(ListDto::id eq listId, ListDto::user_id eq userId),
                push(ListDto::categories, categoryDto)
            )
        }

        fun update(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>, clientCategoryDto: ClientCategoryDto): ListDto? {
            return col.findOneAndUpdate(
                and(ListDto::id eq listId, ListDto::user_id eq userId, (ListDto::categories / CategoryDto::id) eq categoryId),
                set(
                    (ListDto::categories.posOp / CategoryDto::name) setTo clientCategoryDto.name,
                    (ListDto::categories.posOp / CategoryDto::color) setTo clientCategoryDto.color,
                ),
                FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
            )
        }

        fun delete(userId: Id<UserDto>, listId: Id<ListDto>, categoryId: Id<CategoryDto>): ListDto? {
            return col.findOneAndUpdate(
                and(ListDto::id eq listId, ListDto::user_id eq userId),
                pullByFilter((ListDto::categories / CategoryDto::id) eq categoryId)
            )
        }
    }

    fun getAll(userId: Id<UserDto>): List<ListDto> {
        return col.find(ListDto::user_id eq userId).toList()
    }

    fun create(listDto: ListDto) {
        col.save(listDto)
    }

    fun update(userId: Id<UserDto>, listId: Id<ListDto>, clientListDto: ClientListDto): ListDto? {
        return col.findOneAndUpdate(
            and(ListDto::id eq listId, ListDto::user_id eq userId),
            set(
                ListDto::name setTo clientListDto.name,
                ListDto::icon setTo clientListDto.icon,
                ListDto::color setTo clientListDto.color
            ),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun delete(userId: Id<UserDto>, listId: Id<ListDto>) {
        col.deleteOne(ListDto::user_id eq userId, ListDto::id eq listId)
    }
}
