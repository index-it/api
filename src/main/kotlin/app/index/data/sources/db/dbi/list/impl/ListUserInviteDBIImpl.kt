package app.index.data.sources.db.dbi.list.impl

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.TokenGenerator
import app.index.data.models.lists.ListUserInviteData
import app.index.data.sources.db.dbi.list.ListUserInviteDBI
import app.index.data.sources.db.schemas.lists.ListUserInviteEntity
import app.index.data.sources.db.schemas.lists.ListUserInviteTable
import app.index.data.sources.db.schemas.lists.fromData
import app.index.data.sources.db.schemas.lists.toData
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.deleteWhere
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ListUserInviteDBIImpl(
    private val tokenGenerator: TokenGenerator,
) : ListUserInviteDBI {

    override suspend fun create(listUserInviteData: ListUserInviteData) {
        dbQuery {
            ListUserInviteEntity.new {
                fromData(listUserInviteData)
            }
        }
    }

    override suspend fun get(token: String) =
        dbQuery {
            ListUserInviteEntity
                .find { ListUserInviteTable.token eq tokenGenerator.hashToken(token) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun deleteExpired() {
        dbQuery {
            val currentMillis = DatetimeUtils.currentJavaInstant()

            ListUserInviteTable.deleteWhere {
                expires_at less currentMillis
            }
        }
    }
}
