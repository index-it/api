package app.index_it.data.sources.db.schemas.user

import app.index_it.data.models.user.UserDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId
import java.util.UUID

object UserTable : UUIDTable() {
    val email = varchar("email", 150)
    val passwordHash = varchar("password_hash", 100).nullable()
    val emailVerified = bool("email_verified")
    val creationTimestamp = long("creation_timestamp")
    val creationSource = enumerationByName<UserDto.CreationSource>("creation_source", 10)
}

class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(UserTable)

    val email by UserTable.email
    val passwordHash by UserTable.passwordHash
    val emailVerified by UserTable.emailVerified
    val creationTimestamp by UserTable.creationTimestamp
    val creationSource by UserTable.creationSource
}