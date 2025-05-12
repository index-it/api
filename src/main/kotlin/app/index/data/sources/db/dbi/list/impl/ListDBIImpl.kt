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
    private fun listFilter(listId: IxId<ListData>) = Op.build {
        ListTable.id eq listId.toEntityId(ListTable)
    }

    override suspend fun create(listData: ListData) {
        dbQuery {
            ListEntity.new(listData.id.id) {
                fromData(listData)
            }
        }
    }

    override suspend fun getAllOfUser(id: IxId<UserData>): List<ListData> =
        dbQuery {
            ListEntity
                .find { ListTable.user eq id.toEntityId(UsersTable) }
                .map { it.toData() }
        }

    override suspend fun getListsAccessibleByUser(id: IxId<UserData>): List<ListData> =
        dbQuery {
            val userId = id.toEntityId(UsersTable)

            ListEntity.find {
                (ListTable.user eq userId) or
                        (ListTable.id inSubQuery ListViewerTable.select(ListViewerTable.list).where { ListViewerTable.user eq userId }) or
                        (ListTable.id inSubQuery ListEditorTable.select(ListEditorTable.list).where { ListEditorTable.user eq userId })
            }.map { it.toData() }
        }

    override suspend fun get(
        listId: IxId<ListData>,
    ): ListData? =
        dbQuery {
            ListEntity
                .find { listFilter(listId) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun getListUserAccessInfo(
        listId: IxId<ListData>
    ): List<ListData.ListSingleUserAccessInfoResponseData>? = dbQuery {
        ListEntity
            .find { listFilter(listId) }
            .limit(1)
            .firstOrNull()
            ?.toListSingleUserAccessInfo()
    }

    override suspend fun count(id: IxId<UserData>): Long = dbQuery {
        ListEntity
            .find { ListTable.user eq id.toEntityId(UsersTable) }
            .count()
    }


    override suspend fun update(
        listId: IxId<ListData>,
        listUpdateRequestData: ListData.ListUpdateRequestData,
    ): ListData? =
        dbQuery {
            ListTable.updateReturning(where = { listFilter(listId) }) {
                it[name] = listUpdateRequestData.name
                it[emoji] = listUpdateRequestData.icon
                it[color] = listUpdateRequestData.color
                it[archived] = listUpdateRequestData.archived
                it[public] = listUpdateRequestData.public
                it[edited_at] = DatetimeUtils.currentJavaInstant()
            }.firstOrNull()?.let {
                ListEntity.wrapRow(it).toData()
            }
        }

    override suspend fun addPermissionToUser(
        listId: IxId<ListData>,
        userToAddId: IxId<UserData>,
        editor: Boolean
    ): Unit =
        dbQuery {
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

    override suspend fun removePermissionFromUser(
        listId: IxId<ListData>,
        userToRemoveId: IxId<UserData>
    ): Unit =
        dbQuery {
            ListViewerTable.deleteWhere {
                (user eq userToRemoveId.toEntityId(UsersTable)) and (list eq listId.toEntityId(ListTable))
            }
            ListEditorTable.deleteWhere {
                (user eq userToRemoveId.toEntityId(UsersTable)) and (list eq listId.toEntityId(ListTable))
            }
        }

    override suspend fun delete(listId: IxId<ListData>) : Boolean = dbQuery {
        ListTable.deleteWhere { listFilter(listId) } > 0
    }
}
