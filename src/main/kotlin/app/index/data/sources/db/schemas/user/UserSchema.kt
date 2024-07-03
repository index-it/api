package app.index.data.sources.db.schemas.user

import app.index.data.models.user.UserData
import app.index.data.sources.db.schemas.user.UsersTable.created_at
import app.index.data.sources.db.schemas.user.UsersTable.creation_source
import app.index.data.sources.db.schemas.user.UsersTable.email
import app.index.data.sources.db.schemas.user.UsersTable.email_verified
import app.index.data.sources.db.schemas.user.UsersTable.id
import app.index.data.sources.db.schemas.user.UsersTable.password_hash
import app.index.data.sources.db.toIxId
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

/**
 * users instead of "user" because the latter is a reserved keyword in postgres
 * @property id
 * @property email
 * @property password_hash
 * @property email_verified
 * @property created_at
 * @property creation_source
 */
object UsersTable : UUIDTable() {
    val email = varchar("email", 150).uniqueIndex()
    val password_hash = varchar("password_hash", 100).nullable()
    val email_verified = bool("email_verified")
    val created_at = timestamp("created_at")
    val creation_source = enumerationByName<UserData.CreationSource>("creation_source", 10)
    val stripe_customer_id = varchar("stripe_customer_id", 255).nullable()
    val stripe_subscription_id = varchar("stripe_subscription_id", 255).nullable()
    val stripe_price_id = varchar("stripe_price_id", 255).nullable()
}

/**
 * @property id
 * @property email
 * @property password_hash
 * @property email_verified
 * @property created_at
 * @property creation_source
 */
class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(UsersTable)

    var email by UsersTable.email
    var password_hash by UsersTable.password_hash
    var email_verified by UsersTable.email_verified
    var created_at by UsersTable.created_at
    var creation_source by UsersTable.creation_source
    var stripe_customer_id by UsersTable.stripe_customer_id
    var stripe_subscription_id by UsersTable.stripe_subscription_id
    var stripe_price_id by UsersTable.stripe_price_id
}

fun UserEntity.fromData(userData: UserData) {
    email = userData.email
    password_hash = userData.passwordHash
    email_verified = userData.emailVerified
    created_at = Instant.ofEpochMilli(userData.creationTimestamp)
    creation_source = userData.creationSource
    stripe_customer_id = userData.stripe_customer_id
    stripe_subscription_id = userData.stripe_subscription_id
    stripe_price_id = userData.stripe_price_id
}

fun UserEntity.toData() =
    UserData(
        id = id.toIxId(),
        email = email,
        passwordHash = password_hash,
        emailVerified = email_verified,
        creationTimestamp = created_at.toEpochMilli(),
        creationSource = creation_source,
        stripe_customer_id = stripe_customer_id,
        stripe_subscription_id = stripe_subscription_id,
        stripe_price_id = stripe_price_id
    )
