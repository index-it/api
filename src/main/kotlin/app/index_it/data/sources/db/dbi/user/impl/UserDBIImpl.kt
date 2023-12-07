package app.index_it.data.sources.db.dbi.user.impl

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.user.UserDBI
import app.index_it.data.sources.db.schemas.user.UserEntity
import app.index_it.data.sources.db.schemas.user.UsersTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

object UserDBIImpl : UserDBI {
    private fun UserEntity.fromDto(userDto: UserDto) {
        email = userDto.email
        passwordHash = userDto.passwordHash
        emailVerified = userDto.emailVerified
        createdAt = userDto.creationTimestamp
        creationSource = userDto.creationSource
    }

    private fun UserEntity.toDto() = UserDto(
        id = id.toIxId(),
        email = email,
        passwordHash = passwordHash,
        emailVerified = emailVerified,
        creationTimestamp = createdAt,
        creationSource = creationSource
    )

    override suspend fun create(userDto: UserDto) {
        dbQuery {
            UserEntity.new(userDto.id.id) {
                fromDto(userDto)
            }
        }
    }

    override suspend fun get(id: IxId<UserDto>): UserDto? = dbQuery {
        UserEntity.findById(id.id)
    }?.toDto()

    override suspend fun get(email: String): UserDto? = dbQuery {
        UserEntity
            .find { UsersTable.email eq email }
            .limit(1)
            .firstOrNull()
            ?.toDto()
    }

    override suspend fun verifyEmail(id: IxId<UserDto>) {
        dbQuery {
            UsersTable.update({ UsersTable.id eq id.toEntityId(UsersTable) }) {
                it[emailVerified] = true
            }
        }
    }

    override suspend fun resetPassword(id: IxId<UserDto>, newPasswordHashed: String, verifyEmail: Boolean) {
        dbQuery {
            UsersTable.update({ UsersTable.id eq id.toEntityId(UsersTable) }) {
                if (verifyEmail) {
                    it[emailVerified] = true
                }
                it[passwordHash] = newPasswordHashed
            }
        }
    }

    override suspend fun delete(id: IxId<UserDto>) {
        dbQuery {
            UsersTable.deleteWhere { UsersTable.id eq id.toEntityId(UsersTable) }
        }
    }

}