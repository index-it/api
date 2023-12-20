package app.index_it.data.sources.db.dbi.list.impl

import app.index_it.core.logic.DatetimeUtils
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.list.ItemDBI
import app.index_it.data.sources.db.schemas.lists.*
import app.index_it.data.sources.db.schemas.tasks.TaskTable
import app.index_it.data.sources.db.schemas.user.UsersTable
import app.index_it.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ItemDBIImpl : ItemDBI {
    private fun userFilter(userId: IxId<UserDto>) = Op.build { ItemTable.user eq userId.toEntityId(UsersTable) }
    private fun userAndItemFilter(userId: IxId<UserDto>, itemId: IxId<ItemDto>) = Op.build { (ItemTable.id eq itemId.toEntityId(ItemTable)) and userFilter(userId) }

    override suspend fun create(itemDto: ItemDto) {
        dbQuery {
            ItemEntity.new(itemDto.id.id) {
                fromDto(itemDto)
            }
        }
    }

    override suspend fun get(userId: IxId<UserDto>, itemId: IxId<ItemDto>): ItemDto? = dbQuery {
        ItemEntity
            .find { userAndItemFilter(userId, itemId) }
            .limit(1)
            .firstOrNull()
            ?.toDto()
    }

    override suspend fun exists(userId: IxId<UserDto>, itemId: IxId<ItemDto>): Boolean = dbQuery {
        get(userId, itemId) != null
    }

    override suspend fun getOfCategory(userId: IxId<UserDto>, categoryId: IxId<CategoryDto>): List<ItemDto> = dbQuery {
        ItemEntity
            .find { userFilter(userId) and (ItemTable.category eq categoryId.toEntityId(CategoryTable)) }
            .map { it.toDto() }
    }

    override suspend fun getOfList(userId: IxId<UserDto>, listId: IxId<ListDto>): List<ItemDto> = dbQuery {
        ItemEntity
            .find { userFilter(userId) and (ItemTable.list eq listId.toEntityId(ListTable)) }
            .map { it.toDto() }
    }

    override suspend fun setCompletion(userId: IxId<UserDto>, itemId: IxId<ItemDto>, completed: Boolean): Boolean = dbQuery {
        ItemTable.update({ userAndItemFilter(userId, itemId) }) {
            it[this.completed] = completed
            it[this.completedAt] = if (completed) DatetimeUtils.currentMillis() else null
        } > 0
    }

    override suspend fun setTaskConnection(userId: IxId<UserDto>, itemId: IxId<ItemDto>, taskId: IxId<TaskDto>?): Boolean = dbQuery {
        ItemTable.update({ userAndItemFilter(userId, itemId) }) {
            it[this.task] = taskId?.toEntityId(TaskTable)
        } > 0
    }

    override suspend fun update(userId: IxId<UserDto>, itemId: IxId<ItemDto>, itemUpdateRequestDto: ItemDto.ItemUpdateRequestDto): Boolean = dbQuery {
        ItemTable.update({ userAndItemFilter(userId, itemId) }) {
            it[name] = itemUpdateRequestDto.name
            it[category] = itemUpdateRequestDto.categoryId.toEntityId(CategoryTable)
            it[editedAt] = DatetimeUtils.currentMillis()
        } > 0
    }

    override suspend fun delete(userId: IxId<UserDto>, itemId: IxId<ItemDto>) {
        dbQuery {
            ItemTable.deleteWhere { userAndItemFilter(userId, itemId) }
        }
    }
}