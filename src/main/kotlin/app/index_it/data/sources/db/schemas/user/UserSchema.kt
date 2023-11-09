package app.index_it.data.sources.db.schemas.user

import app.index_it.data.models.user.UserDto
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

object UserTable : UUIDTable() {
    val email = varchar("email", 150).uniqueIndex()
    val passwordHash = varchar("password_hash", 100).nullable()
    val emailVerified = bool("email_verified")
    val createdAt = long("created_at")
    val creationSource = enumerationByName<UserDto.CreationSource>("creation_source", 10)
}

class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(UserTable)

    val email by UserTable.email
    val passwordHash by UserTable.passwordHash
    val emailVerified by UserTable.emailVerified
    val createdAt by UserTable.createdAt
    val creationSource by UserTable.creationSource
}