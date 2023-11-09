package app.index_it.data.sources.db.schemas.email

import app.index_it.data.sources.db.schemas.user.UserEntity
import app.index_it.data.sources.db.schemas.user.UserTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object EmailVerificationTable : IntIdTable() {
    val token = varchar("token", 100).uniqueIndex()
    val user = reference("user", UserTable).index()
    val expirationTimestamp = long("expiration_timestamp")
    val creationTimestamp = long("creation_timestamp")
}

class EmailVerificationEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EmailVerificationEntity>(EmailVerificationTable)

    val token by EmailVerificationTable.token
    val user by UserEntity referencedOn EmailVerificationTable.user
    val creationTimestamp by EmailVerificationTable.creationTimestamp
    val expirationTimestamp by EmailVerificationTable.expirationTimestamp
}