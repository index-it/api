package app.index.data.sources.db.dbi.user.impl

import app.index.core.logic.TokenGenerator
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.email.EmailVerificationDto
import app.index.data.models.user.UserDto
import app.index.data.sources.db.dbi.user.EmailVerificationDBI
import app.index.data.sources.db.schemas.user.*
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class EmailVerificationDBIImpl(
    private val tokenGenerator: TokenGenerator,
) : EmailVerificationDBI {
    override suspend fun count(id: IxId<UserDto>): Long =
        dbQuery {
            EmailVerificationEntity.count(EmailVerificationTable.user eq id.toEntityId(UsersTable))
        }

    override suspend fun create(emailVerificationDto: EmailVerificationDto) {
        dbQuery {
            EmailVerificationEntity.new {
                fromDto(emailVerificationDto)
            }
        }
    }

    override suspend fun get(token: String) =
        dbQuery {
            EmailVerificationEntity
                .find { EmailVerificationTable.token eq tokenGenerator.hashToken(token) }
                .limit(1)
                .firstOrNull()
                ?.toDto()
        }

    override suspend fun deleteAll(id: IxId<UserDto>) {
        dbQuery {
            EmailVerificationTable.deleteWhere { user eq id.toEntityId(UsersTable) }
        }
    }
}
