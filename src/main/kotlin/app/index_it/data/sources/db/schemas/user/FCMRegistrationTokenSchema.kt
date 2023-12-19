package app.index_it.data.sources.db.schemas.user

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * @property token
 * @property user
 * @property createdAt
 */
object FCMRegistrationTokenTable : IntIdTable() {
    val token = varchar("token", 100).uniqueIndex()
    val user = reference(
        name = "id_user",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE
    ).index()
    val createdAt = long("created_at")
}

class FCMRegistrationTokenEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FCMRegistrationTokenEntity>(FCMRegistrationTokenTable)

    var token by FCMRegistrationTokenTable.token
    var user by FCMRegistrationTokenTable.user
    var createdAt by FCMRegistrationTokenTable.createdAt

    val userEntity by UserEntity referencedOn FCMRegistrationTokenTable.user
}