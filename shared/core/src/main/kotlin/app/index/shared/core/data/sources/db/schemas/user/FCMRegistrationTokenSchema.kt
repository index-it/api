package app.index.shared.core.data.sources.db.schemas.user

import app.index.shared.core.data.models.user.FCMRegistrationTokenData
import app.index.shared.core.data.sources.db.schemas.user.FCMRegistrationTokenTable.created_at
import app.index.shared.core.data.sources.db.schemas.user.FCMRegistrationTokenTable.token
import app.index.shared.core.data.sources.db.schemas.user.FCMRegistrationTokenTable.user
import app.index.shared.core.data.sources.db.toEntityId
import app.index.shared.core.data.sources.db.toIxId
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

/**
 * @property token
 * @property user
 * @property created_at
 */
object FCMRegistrationTokenTable : IntIdTable() {
    val token = varchar("token", 300).uniqueIndex()
    val user = reference(
        name = "id_user",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE,
    ).index()
    val created_at = timestamp("created_at")
}

/**
 * @property token
 * @property user
 * @property created_at
 *
 * @property userEntity
 */
class FCMRegistrationTokenEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FCMRegistrationTokenEntity>(FCMRegistrationTokenTable)

    var token by FCMRegistrationTokenTable.token
    var user by FCMRegistrationTokenTable.user
    var created_at by FCMRegistrationTokenTable.created_at

    @Suppress("MemberVisibilityCanBePrivate")
    val userEntity by UserEntity referencedOn FCMRegistrationTokenTable.user
}

fun FCMRegistrationTokenEntity.fromData(fcmRegistrationTokenData: FCMRegistrationTokenData) {
    token = fcmRegistrationTokenData.token
    user = fcmRegistrationTokenData.userId.toEntityId(UsersTable)
    created_at = Instant.ofEpochMilli(fcmRegistrationTokenData.createdAt)
}

fun FCMRegistrationTokenEntity.toData() =
    FCMRegistrationTokenData(
        token = token,
        userId = user.toIxId(),
        createdAt = created_at.toEpochMilli(),
    )
