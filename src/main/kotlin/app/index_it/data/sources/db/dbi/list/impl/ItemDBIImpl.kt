package app.index_it.data.sources.db.dbi.list.impl

import app.index_it.core.logic.currentMillis
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.sources.db.dbi.list.ItemDBI
import app.index_it.data.sources.db.schemas.lists.CategoryTable
import app.index_it.data.sources.db.schemas.lists.ItemEntity
import app.index_it.data.sources.db.schemas.lists.ItemTable
import app.index_it.data.sources.db.schemas.lists.ListTable
import app.index_it.data.sources.db.schemas.tasks.TaskTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

object ItemDBIImpl : ItemDBI {
    private fun ItemEntity.fromDto(itemDto: ItemDto) {
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
        listId = list.toIxId(),
        categoryId = category.toIxId(),
        taskId = task?.toIxId(),
        name = name,
        completed = completed,
        createdAt = createdAt,
        editedAt = editedAt,
        completedAt = completedAt
    )

    override suspend fun exists(id: IxId<ItemDto>): Boolean = dbQuery {
        ItemEntity.findById(id.id) != null

    }

    override suspend fun create(itemDto: ItemDto) {
        dbQuery {
            ItemEntity.new(itemDto.id.id) {
                fromDto(itemDto)
            }
        }
    }

    override suspend fun get(id: IxId<ItemDto>): ItemDto? = dbQuery {
        ItemEntity.findById(id.id)?.toDto()
    }

    override suspend fun getOfCategory(id: IxId<CategoryDto>): List<ItemDto> = dbQuery {
        ItemEntity
            .find { ItemTable.category eq id.toEntityId(CategoryTable) }
            .map { it.toDto() }
    }

    override suspend fun getOfList(id: IxId<ListDto>): List<ItemDto> = dbQuery {
        ItemEntity
            .find { ItemTable.list eq id.toEntityId(ListTable) }
            .map { it.toDto() }
    }

    override suspend fun setCompletion(itemId: IxId<ItemDto>, completed: Boolean) {
        dbQuery {
            ItemTable.update({ ItemTable.id eq itemId.toEntityId(ItemTable) }) {
                it[this.completed] = completed
                it[this.completedAt] = if (completed) currentMillis() else null
            }
        }
    }

    override suspend fun setLinking(itemId: IxId<ItemDto>, taskId: IxId<TaskDto>?) {
        dbQuery {
            ItemTable.update({ ItemTable.id eq itemId.toEntityId(ItemTable) }) {
                it[this.task] = taskId?.toEntityId(TaskTable)
            }
        }
    }

    override suspend fun update(id: IxId<ItemDto>, itemUpdateRequestDto: ItemDto.ItemUpdateRequestDto) {
        ItemTable.update({ ItemTable.id eq id.toEntityId(ItemTable) }) {
            it[name] = itemUpdateRequestDto.name
            it[category] = itemUpdateRequestDto.categoryId.toEntityId(CategoryTable)
            it[editedAt] = currentMillis()
        }
    }

    override suspend fun delete(id: IxId<ItemDto>) {
        dbQuery {
            ItemTable.deleteWhere { ItemTable.id eq id.toEntityId(ItemTable) }
        }
    }
}