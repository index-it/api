package app.index.data.sources.db.dbi.list.impl

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ListData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.list.ListDBI
import app.index.data.sources.db.schemas.lists.*
import app.index.data.sources.db.schemas.user.UsersTable
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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

    override suspend fun getByIdOnly(
        listId: IxId<ListData>,
    ): ListData? =
        dbQuery {
            ListEntity
                .findById(listId.id)
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
                it[emoji] = listUpdateRequestData.icon
                it[color] = listUpdateRequestData.color
                it[public] = listUpdateRequestData.public
                it[edited_at] = DatetimeUtils.currentJavaInstant()
            } > 0
        }

    override suspend fun addPermissionToUser(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        userToAddId: IxId<UserData>,
        editor: Boolean
    ): Boolean =
        dbQuery {
            val exists = ListTable.select { userAndListFilter(userId, listId) }.limit(1).firstOrNull() != null

            if (exists) {
                if (editor) {
                    ListViewerTable.deleteWhere {
                        (user eq userToAddId.toEntityId(UsersTable)) and (list eq listId.toEntityId(ListTable))
                    }
                    ListEditorTable.upsert {
                        it[user] = userToAddId.toEntityId(UsersTable)
                        it[list] = listId.toEntityId(ListTable)
                    }
                } else {
                    ListEditorTable.deleteWhere {
                        (user eq userToAddId.toEntityId(UsersTable)) and (list eq listId.toEntityId(ListTable))
                    }
                    ListViewerTable.upsert {
                        it[user] = userToAddId.toEntityId(UsersTable)
                        it[list] = listId.toEntityId(ListTable)
                    }
                }
            }

            return@dbQuery exists
        }

    override suspend fun removePermissionFromUser(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
        userToRemoveId: IxId<UserData>
    ): Boolean =
        dbQuery {
            val exists = ListTable.select { userAndListFilter(userId, listId) }.limit(1).firstOrNull() != null

            if (exists) {
                ListViewerTable.deleteWhere {
                    (user eq userToRemoveId.toEntityId(UsersTable)) and (list eq listId.toEntityId(ListTable))
                }
                ListEditorTable.deleteWhere {
                    (user eq userToRemoveId.toEntityId(UsersTable)) and (list eq listId.toEntityId(ListTable))
                }
            }

            return@dbQuery exists
        }

    override suspend fun delete(
        userId: IxId<UserData>,
        listId: IxId<ListData>,
    ) : Boolean = dbQuery {
        ListTable.deleteWhere { userAndListFilter(userId, listId) } > 0
    }
}
