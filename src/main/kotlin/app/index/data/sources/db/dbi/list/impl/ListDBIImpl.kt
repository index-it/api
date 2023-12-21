package app.index.data.sources.db.dbi.list.impl

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ListData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.list.ListDBI
import app.index.data.sources.db.schemas.lists.ListEntity
import app.index.data.sources.db.schemas.lists.ListTable
import app.index.data.sources.db.schemas.lists.fromData
import app.index.data.sources.db.schemas.lists.toData
import app.index.data.sources.db.schemas.user.UsersTable
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ListDBIImpl : ListDBI {
    private fun userAndListFilter(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ) = Op.build { (ListTable.id eq listId.toEntityId(ListTable)) and (ListTable.user eq userId.toEntityId(UsersTable)) }

    override suspend fun create(listData: ListData) {
        dbQuery {
            ListEntity.new(listData.id.id) {
                fromData(listData)
            }
        }
    }

    override suspend fun get(id: IxId<UserData>): List<ListData> =
        dbQuery {
            ListEntity
                .find { ListTable.user eq id.toEntityId(UsersTable) }
                .map { it.toData() }
        }

    override suspend fun get(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ): ListData? =
        dbQuery {
            ListEntity
                .find { userAndListFilter(userId, listId) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun update(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        listUpdateRequestData: ListData.ListUpdateRequestData,
    ): Boolean =
        dbQuery {
            ListTable.update({ userAndListFilter(userId, listId) }) {
                it[name] = listUpdateRequestData.name
                it[emoji] = listUpdateRequestData.icon.first()
                it[color] = listUpdateRequestData.color
                it[editedAt] = DatetimeUtils.currentMillis()
            } > 0
        }

    override suspend fun delete(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ) {
        dbQuery {
            ListTable.deleteWhere { userAndListFilter(userId, listId) }
        }
    }
}
