package app.index.data.sources.db.dbi.user.impl

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.FCMRegistrationTokenData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.user.FCMRegistrationTokenDBI
import app.index.data.sources.db.schemas.user.*
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single

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
                .limit(1)
                .map { it.toData() }
        }
    }

    override suspend fun update(fcmRegistrationToken: FCMRegistrationTokenData) {
        dbQuery {
            FCMRegistrationTokenTable.update({ FCMRegistrationTokenTable.token eq fcmRegistrationToken.token }) {
                it[user] = fcmRegistrationToken.userId.toEntityId(UsersTable)
                it[createdAt] = fcmRegistrationToken.createdAt
            }
        }
    }

    override suspend fun delete(tokenToDelete: String) {
        dbQuery {
            FCMRegistrationTokenTable.deleteWhere { token eq tokenToDelete }
        }
    }

    override suspend fun deleteExpired() {
        dbQuery {
            val maxAge = DatetimeUtils.currentMillis() - DatetimeUtils.ONE_DAY_MILLIS * 60

            FCMRegistrationTokenTable.deleteWhere {
                createdAt less maxAge
            }
        }
    }
}
