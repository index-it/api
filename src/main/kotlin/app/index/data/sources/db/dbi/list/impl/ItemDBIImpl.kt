package app.index.data.sources.db.dbi.list.impl

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ItemData
import app.index.data.models.lists.ListData
import app.index.data.models.tasks.TaskData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.list.ItemDBI
import app.index.data.sources.db.schemas.lists.*
import app.index.data.sources.db.schemas.tasks.TaskTable
import app.index.data.sources.db.schemas.user.UsersTable
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ItemDBIImpl : ItemDBI {
    private fun userFilter(userId: IxId<UserData>) = Op.build { ItemTable.user eq userId.toEntityId(UsersTable) }

    private fun userAndItemFilter(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ) = Op.build {
        (ItemTable.id eq itemId.toEntityId(ItemTable)) and userFilter(userId)
    }

    override suspend fun create(itemData: ItemData) {
        dbQuery {
            ItemEntity.new(itemData.id.id) {
                fromData(itemData)
            }
        }
    }

    override suspend fun get(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ): ItemData? =
        dbQuery {
            ItemEntity
                .find { userAndItemFilter(userId, itemId) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun exists(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ): Boolean =
        dbQuery {
            get(userId, itemId) != null
        }

    override suspend fun getOfList(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ): List<ItemData> =
        dbQuery {
            ItemEntity
                .find { userFilter(userId) and (ItemTable.list eq listId.toEntityId(ListTable)) }
                .map { it.toData() }
        }

    override suspend fun setCompletion(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
        completed: Boolean,
    ): Boolean =
        dbQuery {
            ItemTable.update({ userAndItemFilter(userId, itemId) }) {
                it[this.completed] = completed
                it[this.completed_at] = if (completed) DatetimeUtils.currentJavaInstant() else null
            } > 0
        }

    override suspend fun setTaskConnection(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
        taskId: IxId<TaskData>?,
    ): Boolean =
        dbQuery {
            ItemTable.update({ userAndItemFilter(userId, itemId) }) {
                it[this.task] = taskId?.toEntityId(TaskTable)
            } > 0
        }

    override suspend fun update(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
        itemUpdateRequestData: ItemData.ItemUpdateRequestData,
    ): Boolean =
        dbQuery {
            ItemTable.update({ userAndItemFilter(userId, itemId) }) {
                it[name] = itemUpdateRequestData.name
                it[category] = itemUpdateRequestData.category_id.toEntityId(CategoryTable)
                it[edited_at] = DatetimeUtils.currentJavaInstant()
            } > 0
        }

    override suspend fun delete(
        userId: IxId<UserData>,
        itemId: IxId<ItemData>,
    ) : Boolean = dbQuery {
        ItemTable.deleteWhere { userAndItemFilter(userId, itemId) } > 0
    }
}
