package app.index_it.data.sources.db.schemas.user

import app.index_it.data.sources.db.schemas.user.PasswordResetTable.createdAt
import app.index_it.data.sources.db.schemas.user.PasswordResetTable.expiresAt
import app.index_it.data.sources.db.schemas.user.PasswordResetTable.id
import app.index_it.data.sources.db.schemas.user.PasswordResetTable.token
import app.index_it.data.sources.db.schemas.user.PasswordResetTable.user
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

// TODO: Job to delete expired stuff
/**
 * @property id
 * @property token
 * @property user
 * @property createdAt
 * @property expiresAt
 */
object PasswordResetTable : IntIdTable() {
    val token = varchar("token", 100).uniqueIndex()
    val user = reference(
        name = "id_user",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE
    ).index()
    val createdAt = long("created_at")
    val expiresAt =  long("expires_at")
}

/**
 * @property id
 * @property token
 * @property user
 * @property createdAt
 * @property expiresAt
 * @property userEntity
 */
class PasswordResetEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PasswordResetEntity>(PasswordResetTable)

    var token by PasswordResetTable.token
    var user by PasswordResetTable.user
    var createdAt by PasswordResetTable.createdAt
    var expiresAt by PasswordResetTable.expiresAt

    val userEntity by UserEntity referencedOn PasswordResetTable.user
}