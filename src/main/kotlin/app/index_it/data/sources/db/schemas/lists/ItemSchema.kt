package app.index_it.data.sources.db.schemas.lists

import app.index_it.data.sources.db.schemas.lists.ItemTable.category
import app.index_it.data.sources.db.schemas.lists.ItemTable.completed
import app.index_it.data.sources.db.schemas.lists.ItemTable.completedAt
import app.index_it.data.sources.db.schemas.lists.ItemTable.createdAt
import app.index_it.data.sources.db.schemas.lists.ItemTable.editedAt
import app.index_it.data.sources.db.schemas.lists.ItemTable.id
import app.index_it.data.sources.db.schemas.lists.ItemTable.list
import app.index_it.data.sources.db.schemas.lists.ItemTable.name
import app.index_it.data.sources.db.schemas.lists.ItemTable.task
import app.index_it.data.sources.db.schemas.tasks.TaskEntity
import app.index_it.data.sources.db.schemas.tasks.TaskTable
import app.index_it.data.sources.db.schemas.user.UserEntity
import app.index_it.data.sources.db.schemas.user.UsersTable
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
    val user = reference(
        name = "id_user",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE
    ).index()
    val list = reference(
        name = "id_list",
        foreign = ListTable,
        onDelete = ReferenceOption.CASCADE
    ).index()
    val category = reference(
        name = "id_category",
        foreign = CategoryTable,
        onDelete = ReferenceOption.CASCADE
    ).index()
    val task = reference(
        name = "id_task",
        foreign = TaskTable,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()
    val name = varchar("ix_name", 150)
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

    var user by ItemTable.user
    var list by ItemTable.list
    var category by ItemTable.category
    var task by ItemTable.task
    var name by ItemTable.name
    var completed by ItemTable.completed
    var createdAt by ItemTable.createdAt
    var editedAt by ItemTable.editedAt
    var completedAt by ItemTable.completedAt

    val userEntity by UserEntity referencedOn ItemTable.user
    val listEntity by ListEntity referencedOn ItemTable.list
    val categoryEntity by CategoryEntity referencedOn ItemTable.category
    val taskEntity by TaskEntity optionalReferencedOn ItemTable.task
}