package app.index_it.data.sources.db.schemas.user

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object EmailVerificationTable : IntIdTable() {
    val token = varchar("token", 100).uniqueIndex()
    val user = reference("user", UserTable).index()
    val createdAt = long("created_at")
    val expiresAt = long("expires_at")
}

class EmailVerificationEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EmailVerificationEntity>(EmailVerificationTable)

    val token by EmailVerificationTable.token
    val user by UserEntity referencedOn EmailVerificationTable.user
    val createdAt by EmailVerificationTable.createdAt
    val expiresAt by EmailVerificationTable.expiresAt
}