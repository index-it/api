package app.index.data.sources.db.dbi.user.impl

import app.index.core.logic.TokenGenerator
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.PasswordResetDto
import app.index.data.models.user.UserDto
import app.index.data.sources.db.dbi.user.PasswordResetDBI
import app.index.data.sources.db.schemas.user.*
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class PasswordResetDBIImpl(
    private val tokenGenerator: TokenGenerator,
) : PasswordResetDBI {
    override suspend fun count(id: IxId<UserDto>): Long =
        dbQuery {
            PasswordResetEntity.count(PasswordResetTable.user eq id.toEntityId(UsersTable))
        }

    override suspend fun create(passwordResetDto: PasswordResetDto) {
        dbQuery {
            PasswordResetEntity.new {
                fromDto(passwordResetDto)
            }
        }
    }

    override suspend fun get(token: String) =
        dbQuery {
            PasswordResetEntity
                .find { PasswordResetTable.token eq tokenGenerator.hashToken(token) }
                .limit(1)
                .firstOrNull()
                ?.toDto()
        }

    override suspend fun deleteAll(id: IxId<UserDto>) {
        dbQuery {
            PasswordResetTable.deleteWhere { user eq id.toEntityId(UsersTable) }
        }
    }
}
