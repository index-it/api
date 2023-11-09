package app.index_it.data.sources.db.schemas.lists

import app.index_it.data.sources.db.schemas.user.UserEntity
import app.index_it.data.sources.db.schemas.user.UserTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

object ListTable : UUIDTable() {
    val user = reference("user", UserTable).index()
    val name = varchar("name", 100)
    val emoji = char("emoji")
    val color = char("color", 7)
    val createdAt = long("created_at")
    val editedAt = long("edited_at").nullable()
}

class ListEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ListEntity>(ListTable)

    val user by UserEntity referencedOn ListTable.user
    val name by ListTable.name
    val emoji by ListTable.emoji
    val color by ListTable.color
    val createdAt by ListTable.createdAt
    val editedAt by ListTable.editedAt
}