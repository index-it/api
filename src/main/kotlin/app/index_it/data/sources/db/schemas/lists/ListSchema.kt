package app.index_it.data.sources.db.schemas.lists

import app.index_it.data.sources.db.schemas.lists.ListTable.color
import app.index_it.data.sources.db.schemas.lists.ListTable.createdAt
import app.index_it.data.sources.db.schemas.lists.ListTable.editedAt
import app.index_it.data.sources.db.schemas.lists.ListTable.emoji
import app.index_it.data.sources.db.schemas.lists.ListTable.id
import app.index_it.data.sources.db.schemas.lists.ListTable.name
import app.index_it.data.sources.db.schemas.lists.ListTable.user
import app.index_it.data.sources.db.schemas.user.UserEntity
import app.index_it.data.sources.db.schemas.user.UserTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*

/**
 * @property id
 * @property user
 * @property name
 * @property emoji
 * @property color
 * @property createdAt
 * @property editedAt
 */
object ListTable : UUIDTable() {
    val user = reference(
        name = "user",
        foreign = UserTable,
        onDelete = ReferenceOption.CASCADE
    ).index()
    val name = varchar("name", 100)
    val emoji = char("emoji")
    val color = varchar("color", 9)
    val createdAt = long("created_at")
    val editedAt = long("edited_at").nullable()
}

/**
 * @property id
 * @property user
 * @property name
 * @property emoji
 * @property color
 * @property createdAt
 * @property editedAt
 * @property userEntity
 */
class ListEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ListEntity>(ListTable)

    var user by ListTable.user
    var name by ListTable.name
    var emoji by ListTable.emoji
    var color by ListTable.color
    var createdAt by ListTable.createdAt
    var editedAt by ListTable.editedAt

    val userEntity by UserEntity referencedOn ListTable.user
}