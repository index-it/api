package app.index.api.data.sources.db.dbi.user.impl

import app.index.shared.core.logic.DatetimeUtils
import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.user.FCMRegistrationTokenData
import app.index.shared.core.data.models.user.UserData
import app.index.api.data.sources.db.dbi.user.FCMRegistrationTokenDBI
import app.index.api.data.sources.db.schemas.user.*
import app.index.api.data.sources.db.toEntityId
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import java.time.Instant
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

@Single(createdAtStart = true)
class FCMRegistrationTokenDBIImpl : FCMRegistrationTokenDBI {
    override suspend fun exists(token: String): Boolean =
        dbQuery {
            FCMRegistrationTokenEntity.count(FCMRegistrationTokenTable.token eq token) > 0
        }

    override suspend fun create(fcmRegistrationToken: FCMRegistrationTokenData) {
        dbQuery {
            FCMRegistrationTokenEntity.new {
                fromData(fcmRegistrationToken)
            }
        }
    }

    override suspend fun get(token: String): FCMRegistrationTokenData? {
        return dbQuery {
            FCMRegistrationTokenEntity
                .find { FCMRegistrationTokenTable.token eq token }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }
    }

    override suspend fun getOfUser(id: IxId<UserData>): List<FCMRegistrationTokenData> {
        return dbQuery {
            FCMRegistrationTokenEntity
                .find { FCMRegistrationTokenTable.user eq id.toEntityId(UsersTable) }
                .map { it.toData() }
        }
    }

    override suspend fun update(fcmRegistrationToken: FCMRegistrationTokenData) {
        dbQuery {
            FCMRegistrationTokenTable.update({ FCMRegistrationTokenTable.token eq fcmRegistrationToken.token }) {
                it[user] = fcmRegistrationToken.userId.toEntityId(UsersTable)
                it[created_at] = Instant.ofEpochMilli(fcmRegistrationToken.createdAt)
            }
        }
    }

    override suspend fun delete(tokenToDelete: String) {
        dbQuery {
            FCMRegistrationTokenTable.deleteWhere { token eq tokenToDelete }
        }
    }

    @ExperimentalTime
    override suspend fun deleteExpired() {
        dbQuery {
            val maxAge = DatetimeUtils.currentInstant().minus(60, DateTimeUnit.DAY, DatetimeUtils.utcTimeZone).toJavaInstant()

            FCMRegistrationTokenTable.deleteWhere {
                created_at less maxAge
            }
        }
    }
}
