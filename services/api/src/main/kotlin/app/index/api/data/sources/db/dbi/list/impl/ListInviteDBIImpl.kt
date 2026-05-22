package app.index.api.data.sources.db.dbi.list.impl

import app.index.shared.core.logic.DatetimeUtils
import app.index.api.core.logic.TokenGenerator
import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.lists.ListData
import app.index.shared.core.data.models.lists.ListInviteData
import app.index.api.data.sources.db.dbi.list.ListInviteDBI
import app.index.api.data.sources.db.schemas.lists.*
import app.index.api.data.sources.db.toEntityId
import kotlinx.datetime.toJavaLocalDateTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.updateReturning
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ListInviteDBIImpl(
    private val tokenGenerator: TokenGenerator,
) : ListInviteDBI {

    override suspend fun create(listInviteData: ListInviteData) {
        dbQuery {
            ListInviteEntity.new {
                fromData(listInviteData)
            }
        }
    }

    override suspend fun decreaseUsages(id: IxId<ListInviteData>): ListInviteData? =
        dbQuery {
            ListInviteTable.updateReturning(where = { ListInviteTable.id eq id.toEntityId(ListInviteTable) }) {
                it[this.max_usages] = max_usages - 1
            }.firstOrNull()?.let { ListInviteEntity.wrapRow(it).toData() }
        }

    override suspend fun get(token: String) =
        dbQuery {
            ListInviteEntity
                .find { ListInviteTable.token eq tokenGenerator.hashToken(token) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun get(listId: IxId<ListData>): List<ListInviteData> =
        dbQuery {
            ListInviteEntity
                .find { ListInviteTable.list eq listId.toEntityId(ListTable) }
                .map { it.toData() }
        }

    override suspend fun delete(id: IxId<ListInviteData>) = dbQuery {
        ListInviteTable.deleteWhere { ListInviteTable.id eq id.toEntityId(ListInviteTable) } > 0
    }

    override suspend fun deleteExpired() {
        dbQuery {
            val currentDateTime = DatetimeUtils.currentLocalDateTime().toJavaLocalDateTime()

            ListInviteTable.deleteWhere {
                (expires_at.isNotNull()) and (expires_at less currentDateTime)
            }
        }
    }
}
