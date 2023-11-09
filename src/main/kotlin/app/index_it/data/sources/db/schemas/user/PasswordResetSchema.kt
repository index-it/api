package app.index_it.data.sources.db.schemas.user

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

// TODO: Job to delete expired stuff
object PasswordResetTable : IntIdTable() {
    val token = varchar("token", 100).uniqueIndex()
    val user = reference("user", UserTable).index()
    val createdAt = long("created_at")
    val expiresAt =  long("expires_at")
}

class PasswordResetEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PasswordResetEntity>(PasswordResetTable)

    val token by PasswordResetTable.token
    val user by UserEntity referencedOn PasswordResetTable.user
    val createdAt by PasswordResetTable.createdAt
    val expiresAt by PasswordResetTable.expiresAt
}