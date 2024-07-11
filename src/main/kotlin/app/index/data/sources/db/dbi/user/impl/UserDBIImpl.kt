package app.index.data.sources.db.dbi.user.impl

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.user.UserDBI
import app.index.data.sources.db.schemas.user.UserEntity
import app.index.data.sources.db.schemas.user.UsersTable
import app.index.data.sources.db.schemas.user.fromData
import app.index.data.sources.db.schemas.user.toData
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.updateReturning
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class UserDBIImpl : UserDBI {
    override suspend fun create(userData: UserData) {
        dbQuery {
            UserEntity.new(userData.id.id) {
                fromData(userData)
            }
        }
    }

    override suspend fun get(id: IxId<UserData>): UserData? =
        dbQuery {
            UserEntity.findById(id.id)
        }?.toData()

    override suspend fun get(email: String): UserData? =
        dbQuery {
            UserEntity
                .find { UsersTable.email eq email }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun verifyEmail(id: IxId<UserData>) {
        dbQuery {
            UsersTable.update({ UsersTable.id eq id.toEntityId(UsersTable) }) {
                it[email_verified] = true
            }
        }
    }

    override suspend fun changePassword(id: IxId<UserData>, newPasswordHashed: String) {
        dbQuery {
            UsersTable.update({ UsersTable.id eq id.toEntityId(UsersTable) }) {
                it[password_hash] = newPasswordHashed
            }
        }
    }

    override suspend fun resetPassword(
        id: IxId<UserData>,
        newPasswordHashed: String,
        verifyEmail: Boolean,
    ) {
        dbQuery {
            UsersTable.update({ UsersTable.id eq id.toEntityId(UsersTable) }) {
                if (verifyEmail) {
                    it[email_verified] = true
                }
                it[password_hash] = newPasswordHashed
            }
        }
    }

    override suspend fun setHasPro(id: IxId<UserData>, hasPro: Boolean): UserData? {
        return dbQuery {
            UsersTable.updateReturning(where = { UsersTable.id eq id.toEntityId(UsersTable) }) {
                it[has_pro] = hasPro
            }.firstOrNull()?.let {
                UserEntity.wrapRow(it).toData()
            }
        }
    }

    override suspend fun delete(id: IxId<UserData>) {
        dbQuery {
            UsersTable.deleteWhere { UsersTable.id eq id.toEntityId(UsersTable) }
        }
    }
}
