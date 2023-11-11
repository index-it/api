package app.index_it.data.sources.db.schemas.lists

import app.index_it.data.sources.db.schemas.tasks.TaskEntity
import app.index_it.data.sources.db.schemas.tasks.TaskTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*

/**
 * @property id
 * @property list
 * @property category
 * @property task
 * @property name
 * @property completed
 * @property createdAt
 * @property editedAt
 * @property completedAt
 */
object ItemTable : UUIDTable() {
    // val user = reference("user", UserTable).index()
    val list = reference(
        name = "list",
        foreign = ListTable,
        onDelete = ReferenceOption.CASCADE
    ).index()
    val category = reference(
        name = "category",
        foreign = CategoryTable,
        onDelete = ReferenceOption.CASCADE
    )
    val task = reference("task", TaskTable).nullable()
    val name = varchar("name", 150)
    val completed = bool("completed")
    val createdAt = long("created_at")
    val editedAt = long("edited_at").nullable()
    val completedAt = long("completed_at").nullable()
}

/**
 * @property id
 * @property list
 * @property category
 * @property task
 * @property name
 * @property completed
 * @property createdAt
 * @property editedAt
 * @property completedAt
 * @property listEntity
 * @property categoryEntity
 * @property taskEntity
 */
class ItemEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ItemEntity>(ItemTable)

    // val user by UserEntity referencedOn ItemTable.user
    var list by ItemTable.list
    var category by ItemTable.category
    var task by ItemTable.task
    var name by ItemTable.name
    var completed by ItemTable.completed
    var createdAt by ItemTable.createdAt
    var editedAt by ItemTable.editedAt
    var completedAt by ItemTable.completedAt

    val listEntity by ListEntity referencedOn ItemTable.list
    val categoryEntity by CategoryEntity referencedOn ItemTable.category
    val taskEntity by TaskEntity referencedOn ItemTable.task
}