package app.index_it.data.sources.db.schemas.user

import app.index_it.data.sources.db.schemas.user.EmailVerificationTable.createdAt
import app.index_it.data.sources.db.schemas.user.EmailVerificationTable.expiresAt
import app.index_it.data.sources.db.schemas.user.EmailVerificationTable.id
import app.index_it.data.sources.db.schemas.user.EmailVerificationTable.token
import app.index_it.data.sources.db.schemas.user.EmailVerificationTable.user
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * @property id
 * @property token
 * @property user
 * @property createdAt
 * @property expiresAt
 */
object EmailVerificationTable : IntIdTable() {
    val token = varchar("token", 100).uniqueIndex()
    val user = reference(
        name = "user",
        foreign = UserTable,
        onDelete = ReferenceOption.CASCADE
    ).index()
    val createdAt = long("created_at")
    val expiresAt = long("expires_at")
}

/**
 * @property id
 * @property token
 * @property user
 * @property createdAt
 * @property expiresAt
 * @property userEntity
 */
class EmailVerificationEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EmailVerificationEntity>(EmailVerificationTable)

    var token by EmailVerificationTable.token
    var user by EmailVerificationTable.user
    var createdAt by EmailVerificationTable.createdAt
    var expiresAt by EmailVerificationTable.expiresAt

    var userEntity by UserEntity referencedOn EmailVerificationTable.user
}