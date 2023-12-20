package app.index_it.data.sources.db.dbi.user.impl

import app.index_it.core.logic.TokenGenerator
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.PasswordResetDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.user.PasswordResetDBI
import app.index_it.data.sources.db.schemas.user.*
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class PasswordResetDBIImpl(
    private val tokenGenerator: TokenGenerator
) : PasswordResetDBI {
    override suspend fun count(id: IxId<UserDto>): Long = dbQuery {
        PasswordResetEntity.count(PasswordResetTable.user eq id.toEntityId(UsersTable))
    }

    override suspend fun create(passwordResetDto: PasswordResetDto) {
        dbQuery {
            PasswordResetEntity.new {
                fromDto(passwordResetDto)
            }
        }
    }

    override suspend fun get(token: String) = dbQuery {
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