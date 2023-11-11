package app.index_it.data.sources.db.dbi.user.impl

import app.index_it.core.logic.TokenGenerator
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.email.EmailVerificationDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.user.EmailVerificationDBI
import app.index_it.data.sources.db.schemas.user.EmailVerificationEntity
import app.index_it.data.sources.db.schemas.user.EmailVerificationTable
import app.index_it.data.sources.db.schemas.user.UserTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

object EmailVerificationDBIImpl : EmailVerificationDBI {
    private fun EmailVerificationEntity.fromDto(emailVerificationDto: EmailVerificationDto) {
        token = emailVerificationDto.token
        user = emailVerificationDto.userId.toEntityId(UserTable)
    }

    private fun EmailVerificationEntity.toDto() = EmailVerificationDto(
        token = token,
        userId = user.toIxId(),
        expireAt = expiresAt,
        createdAt = createdAt
    )

    override suspend fun count(id: IxId<UserDto>): Long = dbQuery {
        EmailVerificationEntity.count(EmailVerificationTable.user eq id.toEntityId(UserTable))
    }

    override suspend fun save(emailVerificationDto: EmailVerificationDto) {
        dbQuery {
            EmailVerificationEntity.new {
                fromDto(emailVerificationDto)
            }
        }
    }

    override suspend fun get(token: String): EmailVerificationDto? = dbQuery {
        EmailVerificationEntity
            .find { EmailVerificationTable.token eq TokenGenerator.hashToken(token) }
            .limit(1)
            .firstOrNull()
            ?.toDto()
    }

    override suspend fun deleteAll(id: IxId<UserDto>) {
        dbQuery {
            EmailVerificationTable.deleteWhere { user eq id.toEntityId(UserTable) }
        }
    }
}