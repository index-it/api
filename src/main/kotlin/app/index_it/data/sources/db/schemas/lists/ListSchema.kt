package app.index_it.data.sources.db.schemas.lists

import app.index_it.data.models.lists.ListDto
import app.index_it.data.sources.db.schemas.lists.ListTable.color
import app.index_it.data.sources.db.schemas.lists.ListTable.createdAt
import app.index_it.data.sources.db.schemas.lists.ListTable.editedAt
import app.index_it.data.sources.db.schemas.lists.ListTable.emoji
import app.index_it.data.sources.db.schemas.lists.ListTable.id
import app.index_it.data.sources.db.schemas.lists.ListTable.name
import app.index_it.data.sources.db.schemas.lists.ListTable.user
import app.index_it.data.sources.db.schemas.user.UserEntity
import app.index_it.data.sources.db.schemas.user.UsersTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
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
        name = "id_user",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE
    ).index()
    val name = varchar("ix_name", 100)
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

fun ListEntity.fromDto(listDto: ListDto) {
    user = listDto.userId.toEntityId(UsersTable)
    name = listDto.name
    emoji = listDto.icon.first()
    color = listDto.color
    createdAt = listDto.createdAt
    editedAt = listDto.editedAt
}

fun ListEntity.toDto() = ListDto(
    id = id.toIxId(),
    userId = user.toIxId(),
    name = name,
    icon = emoji.toString(),
    color = color,
    createdAt = createdAt,
    editedAt = editedAt
)