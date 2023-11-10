package app.index_it.data.sources.db.schemas.user

import app.index_it.data.models.user.UserDto
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

/**
 * @property id
 * @property email
 * @property passwordHash
 * @property emailVerified
 * @property createdAt
 * @property creationSource
 */
object UserTable : UUIDTable() {
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
    companion object : UUIDEntityClass<UserEntity>(UserTable)

    var email by UserTable.email
    var passwordHash by UserTable.passwordHash
    var emailVerified by UserTable.emailVerified
    var createdAt by UserTable.createdAt
    var creationSource by UserTable.creationSource
}