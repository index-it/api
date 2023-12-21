package app.index.data.sources.db.schemas.user

import app.index.data.models.email.EmailVerificationData
import app.index.data.sources.db.schemas.user.EmailVerificationTable.createdAt
import app.index.data.sources.db.schemas.user.EmailVerificationTable.expiresAt
import app.index.data.sources.db.schemas.user.EmailVerificationTable.id
import app.index.data.sources.db.schemas.user.EmailVerificationTable.token
import app.index.data.sources.db.schemas.user.EmailVerificationTable.user
import app.index.data.sources.db.toEntityId
import app.index.data.sources.db.toIxId
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
    val user =
        reference(
            name = "id_user",
            foreign = UsersTable,
            onDelete = ReferenceOption.CASCADE,
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

fun EmailVerificationEntity.fromData(emailVerificationData: EmailVerificationData) {
    token = emailVerificationData.token
    user = emailVerificationData.userId.toEntityId(UsersTable)
    createdAt = emailVerificationData.createdAt
    expiresAt = emailVerificationData.expireAt
}

fun EmailVerificationEntity.toData() =
    EmailVerificationData(
        token = token,
        userId = user.toIxId(),
        expireAt = expiresAt,
        createdAt = createdAt,
    )
