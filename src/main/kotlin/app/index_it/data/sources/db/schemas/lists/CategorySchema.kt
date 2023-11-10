package app.index_it.data.sources.db.schemas.lists

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*


/**
 * @property id
 * @property list
 * @property name
 * @property color
 */
object CategoryTable : UUIDTable() {
    // val user = reference("user", UserTable).index()
    val list = reference(
        name = "list",
        foreign = ListTable,
        onDelete = ReferenceOption.CASCADE
    ).index()
    val name = varchar("name", 50)
    val color = char("color", 9)
}

/**
 * @property id
 * @property list
 * @property name
 * @property color
 *
 * @property listEntity
 */
class CategoryEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CategoryEntity>(CategoryTable)

    // val user by UserEntity referencedOn CategoryTable.user
    var list by CategoryTable.list
    var name by CategoryTable.name
    var color by CategoryTable.color

    var listEntity by ListEntity referencedOn CategoryTable.list
}