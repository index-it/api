package app.index_it.data.sources.db.schemas.tasks

import app.index_it.data.sources.db.schemas.lists.ItemEntity
import app.index_it.data.sources.db.schemas.lists.ItemTable
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
 * @property item
 * @property name
 * @property description
 * @property dueDate
 * @property completed
 * @property priority
 * @property createdAt
 * @property editedAt
 * @property completedAt
 */
object TaskTable : UUIDTable() {
    val user = reference(
        name = "user",
        foreign = UserTable,
        onDelete = ReferenceOption.CASCADE
    )
    val item = reference("item", ItemTable).nullable()
    val name = varchar("name", 150)
    val description = varchar("description", 500).nullable()
    // subtasks
    val dueDate = long("due_date").nullable()
    val completed = bool("completed")
    val priority = integer("priority").nullable()
    val createdAt = long("created_at")
    val editedAt = long("edited_at").nullable()
    val completedAt = long("completed_at").nullable()
}

/**
 * @property id
 * @property user
 * @property item
 * @property name
 * @property description
 * @property dueDate
 * @property completed
 * @property priority
 * @property createdAt
 * @property editedAt
 * @property completedAt
 * @property userEntity
 * @property itemEntity
 */
class TaskEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<TaskEntity>(TaskTable)

    val user by TaskTable.user
    val item by TaskTable.item
    val name by TaskTable.name
    val description by TaskTable.description
    val dueDate by TaskTable.dueDate
    val completed by TaskTable.completed
    val priority by TaskTable.priority
    val createdAt by TaskTable.createdAt
    val editedAt by TaskTable.editedAt
    val completedAt by TaskTable.completedAt

    val userEntity by UserEntity referencedOn TaskTable.user
    val itemEntity by ItemEntity optionalReferencedOn TaskTable.item
}