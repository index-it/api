package app.index_it.data.sources.db.schemas.user

import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.schemas.user.UsersTable.createdAt
import app.index_it.data.sources.db.schemas.user.UsersTable.creationSource
import app.index_it.data.sources.db.schemas.user.UsersTable.email
import app.index_it.data.sources.db.schemas.user.UsersTable.emailVerified
import app.index_it.data.sources.db.schemas.user.UsersTable.id
import app.index_it.data.sources.db.schemas.user.UsersTable.passwordHash
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

/**
 * users instead of "user" because the latter is a reserved keyword in postgres
 * @property id
 * @property email
 * @property passwordHash
 * @property emailVerified
 * @property createdAt
 * @property creationSource
 */
object UsersTable : UUIDTable() {
    val email = varchar("email", 150).uniqueIndex()
    val passwordHash = varchar("password_hash", 100).nullable()
    val emailVerified = bool("email_verified")
    val createdAt = long("created_at")
    val creationSource = enumerationByName<UserDto.CreationSource>("creation_source", 10)
}

/**
 * @property id
 * @property email
 * @property passwordHash
 * @property emailVerified
 * @property createdAt
 * @property creationSource
 */
class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(UsersTable)

    var email by UsersTable.email
    var passwordHash by UsersTable.passwordHash
    var emailVerified by UsersTable.emailVerified
    var createdAt by UsersTable.createdAt
    var creationSource by UsersTable.creationSource
}

fun UserEntity.fromDto(userDto: UserDto) {
    email = userDto.email
    passwordHash = userDto.passwordHash
    emailVerified = userDto.emailVerified
    createdAt = userDto.creationTimestamp
    creationSource = userDto.creationSource
}

fun UserEntity.toDto() = UserDto(
    id = id.toIxId(),
    email = email,
    passwordHash = passwordHash,
    emailVerified = emailVerified,
    creationTimestamp = createdAt,
    creationSource = creationSource
)