package app.index_it.data.sources.db.schemas.lists

import app.index_it.data.sources.db.schemas.user.UserEntity
import app.index_it.data.sources.db.schemas.user.UserTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object CategoryTable : UUIDTable() {
    // val user = reference("user", UserTable).index()
    val list = reference("list", ListTable).index()
    val name = varchar("name", 50)
    val color = char("color", 9)
}

class CategoryEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CategoryEntity>(CategoryTable)

    // val user by UserEntity referencedOn CategoryTable.user
    val list by ListEntity referencedOn CategoryTable.list
    val name by CategoryTable.name
    val color by CategoryTable.color
}