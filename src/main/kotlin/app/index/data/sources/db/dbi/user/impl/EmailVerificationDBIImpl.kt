package app.index.data.sources.db.dbi.user.impl

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.TokenGenerator
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.email.EmailVerificationData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.user.EmailVerificationDBI
import app.index.data.sources.db.schemas.user.*
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class EmailVerificationDBIImpl(
    private val tokenGenerator: TokenGenerator,
) : EmailVerificationDBI {
    override suspend fun count(id: IxId<UserData>): Long =
        dbQuery {
            val currentMillis = DatetimeUtils.currentJavaInstant()

            EmailVerificationEntity.count(
                EmailVerificationTable.user eq id.toEntityId(UsersTable)
                        and (EmailVerificationTable.expires_at greater currentMillis)
            )
        }

    override suspend fun create(emailVerificationData: EmailVerificationData) {
        dbQuery {
            EmailVerificationEntity.new {
                fromData(emailVerificationData)
            }
        }
    }

    override suspend fun get(token: String) =
        dbQuery {
            EmailVerificationEntity
                .find { EmailVerificationTable.token eq tokenGenerator.hashToken(token) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun deleteAll(id: IxId<UserData>) {
        dbQuery {
            EmailVerificationTable.deleteWhere { user eq id.toEntityId(UsersTable) }
        }
    }

    override suspend fun deleteExpired() {
        dbQuery {
            val currentTimestamp = DatetimeUtils.currentJavaInstant()

            EmailVerificationTable.deleteWhere {
                expires_at less currentTimestamp
            }
        }
    }
}
