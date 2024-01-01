package app.index.data.sources.db.dbi.user.impl

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.TokenGenerator
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.PasswordResetData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.user.PasswordResetDBI
import app.index.data.sources.db.schemas.user.*
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class PasswordResetDBIImpl(
    private val tokenGenerator: TokenGenerator,
) : PasswordResetDBI {
    override suspend fun count(id: IxId<UserData>): Long =
        dbQuery {
            val currentTimestamp = DatetimeUtils.currentJavaInstant()

            PasswordResetEntity.count(
                PasswordResetTable.user eq id.toEntityId(UsersTable)
                        and (PasswordResetTable.expires_at greater currentTimestamp)
            )
        }

    override suspend fun create(passwordResetData: PasswordResetData) {
        dbQuery {
            PasswordResetEntity.new {
                fromData(passwordResetData)
            }
        }
    }

    override suspend fun get(token: String) =
        dbQuery {
            PasswordResetEntity
                .find { PasswordResetTable.token eq tokenGenerator.hashToken(token) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun deleteAll(id: IxId<UserData>) {
        dbQuery {
            PasswordResetTable.deleteWhere { user eq id.toEntityId(UsersTable) }
        }
    }

    override suspend fun deleteExpired() {
        dbQuery {
            val currentMillis = DatetimeUtils.currentJavaInstant()

            PasswordResetTable.deleteWhere {
                expires_at less currentMillis
            }
        }
    }
}
