package app.index_it.data.sources.db.dbi.list.impl

import app.index_it.core.logic.currentMillis
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.list.ItemDBI
import app.index_it.data.sources.db.schemas.lists.CategoryTable
import app.index_it.data.sources.db.schemas.lists.ItemEntity
import app.index_it.data.sources.db.schemas.lists.ItemTable
import app.index_it.data.sources.db.schemas.lists.ListTable
import app.index_it.data.sources.db.schemas.tasks.TaskTable
import app.index_it.data.sources.db.schemas.user.UserTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

object ItemDBIImpl : ItemDBI {
    private fun ItemEntity.fromDto(itemDto: ItemDto) {
        user = itemDto.userId.toEntityId(UserTable)
        list = itemDto.listId.toEntityId(ListTable)
        category = itemDto.categoryId.toEntityId(CategoryTable)
        task = itemDto.taskId?.toEntityId(TaskTable)
        name = itemDto.name
        completed = itemDto.completed
        createdAt = itemDto.createdAt
        editedAt = itemDto.editedAt
        completedAt = itemDto.completedAt
    }

    private fun ItemEntity.toDto() = ItemDto(
        id = id.toIxId(),
        userId = user.toIxId(),
        listId = list.toIxId(),
        categoryId = category.toIxId(),
        taskId = task?.toIxId(),
        name = name,
        completed = completed,
        createdAt = createdAt,
        editedAt = editedAt,
        completedAt = completedAt
    )

    private fun userFilter(userId: IxId<UserDto>) = Op.build { ItemTable.user eq userId.toEntityId(UserTable) }
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
            it[this.completedAt] = if (completed) currentMillis() else null
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
            it[editedAt] = currentMillis()
        } > 0
    }

    override suspend fun delete(userId: IxId<UserDto>, itemId: IxId<ItemDto>) {
        dbQuery {
            ItemTable.deleteWhere { userAndItemFilter(userId, itemId) }
        }
    }
}