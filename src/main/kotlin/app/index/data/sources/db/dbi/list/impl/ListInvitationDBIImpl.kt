package app.index.data.sources.db.dbi.list.impl

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.TokenGenerator
import app.index.data.models.lists.ListInvitationData
import app.index.data.sources.db.dbi.list.ListInvitationDBI
import app.index.data.sources.db.schemas.lists.ListInvitationEntity
import app.index.data.sources.db.schemas.lists.ListInvitationTable
import app.index.data.sources.db.schemas.lists.fromData
import app.index.data.sources.db.schemas.lists.toData
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.deleteWhere
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ListInvitationDBIImpl(
    private val tokenGenerator: TokenGenerator,
) : ListInvitationDBI {

    override suspend fun create(listInvitationData: ListInvitationData) {
        dbQuery {
            ListInvitationEntity.new {
                fromData(listInvitationData)
            }
        }
    }

    override suspend fun get(token: String) =
        dbQuery {
            ListInvitationEntity
                .find { ListInvitationTable.token eq tokenGenerator.hashToken(token) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun deleteExpired() {
        dbQuery {
            val currentMillis = DatetimeUtils.currentJavaInstant()

            ListInvitationTable.deleteWhere {
                expires_at less currentMillis
            }
        }
    }
}
