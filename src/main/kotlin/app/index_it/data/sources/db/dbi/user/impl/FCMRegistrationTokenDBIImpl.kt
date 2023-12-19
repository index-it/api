package app.index_it.data.sources.db.dbi.user.impl

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.FCMRegistrationTokenDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.user.FCMRegistrationTokenDBI
import app.index_it.data.sources.db.schemas.user.*
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

object FCMRegistrationTokenDBIImpl : FCMRegistrationTokenDBI {
    private fun FCMRegistrationTokenEntity.fromDto(fcmRegistrationTokenDto: FCMRegistrationTokenDto) {
        token = fcmRegistrationTokenDto.token
        user = fcmRegistrationTokenDto.userId.toEntityId(UsersTable)
        createdAt = fcmRegistrationTokenDto.createdAt
    }

    private fun FCMRegistrationTokenEntity.toDto() = FCMRegistrationTokenDto(
        token = token,
        userId = user.toIxId(),
        createdAt = createdAt,
    )

    override suspend fun exists(token: String): Boolean = dbQuery {
        FCMRegistrationTokenEntity.count(FCMRegistrationTokenTable.token eq token) > 0
    }

    override suspend fun create(fcmRegistrationToken: FCMRegistrationTokenDto) {
        dbQuery {
            FCMRegistrationTokenEntity.new {
                fromDto(fcmRegistrationToken)
            }
        }
    }

    override suspend fun get(token: String): FCMRegistrationTokenDto? {
        return dbQuery {
            FCMRegistrationTokenEntity
                .find { FCMRegistrationTokenTable.token eq token }
                .limit(1)
                .firstOrNull()
                ?.toDto()
        }
    }

    override suspend fun getOfUser(id: IxId<UserDto>): List<FCMRegistrationTokenDto> {
        return dbQuery {
            FCMRegistrationTokenEntity
                .find { FCMRegistrationTokenTable.user eq id.toEntityId(UsersTable) }
                .limit(1)
                .map { it.toDto() }
        }
    }

    override suspend fun update(fcmRegistrationToken: FCMRegistrationTokenDto) {
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
}