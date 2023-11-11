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

    var user by TaskTable.user
    var item by TaskTable.item
    var name by TaskTable.name
    var description by TaskTable.description
    var dueDate by TaskTable.dueDate
    var completed by TaskTable.completed
    var priority by TaskTable.priority
    var createdAt by TaskTable.createdAt
    var editedAt by TaskTable.editedAt
    var completedAt by TaskTable.completedAt

    val userEntity by UserEntity referencedOn TaskTable.user
    val itemEntity by ItemEntity optionalReferencedOn TaskTable.item
}