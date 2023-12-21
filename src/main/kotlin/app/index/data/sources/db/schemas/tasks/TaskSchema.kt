package app.index.data.sources.db.schemas.tasks

import app.index.data.models.tasks.TaskData
import app.index.data.sources.db.schemas.lists.ItemEntity
import app.index.data.sources.db.schemas.lists.ItemTable
import app.index.data.sources.db.schemas.tasks.TaskTable.completed
import app.index.data.sources.db.schemas.tasks.TaskTable.completedAt
import app.index.data.sources.db.schemas.tasks.TaskTable.createdAt
import app.index.data.sources.db.schemas.tasks.TaskTable.description
import app.index.data.sources.db.schemas.tasks.TaskTable.dueDate
import app.index.data.sources.db.schemas.tasks.TaskTable.editedAt
import app.index.data.sources.db.schemas.tasks.TaskTable.id
import app.index.data.sources.db.schemas.tasks.TaskTable.item
import app.index.data.sources.db.schemas.tasks.TaskTable.name
import app.index.data.sources.db.schemas.tasks.TaskTable.priority
import app.index.data.sources.db.schemas.tasks.TaskTable.rrule
import app.index.data.sources.db.schemas.tasks.TaskTable.user
import app.index.data.sources.db.schemas.user.UserEntity
import app.index.data.sources.db.schemas.user.UsersTable
import app.index.data.sources.db.toEntityId
import app.index.data.sources.db.toIxId
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
 * @property rrule
 * @property dueDateReminder
 * @property completed
 * @property priority
 * @property createdAt
 * @property editedAt
 * @property completedAt
 */
object TaskTable : UUIDTable() {
    val user =
        reference(
            name = "id_user",
            foreign = UsersTable,
            onDelete = ReferenceOption.CASCADE,
        )
    val item =
        reference(
            name = "id_item",
            foreign = ItemTable,
            onDelete = ReferenceOption.SET_NULL,
        ).nullable()
    val name = varchar("ix_name", 150)
    val description = varchar("description", 500).nullable()
    val dueDate = long("due_date").nullable()
    val rrule = varchar("rrule", 200).nullable()
    val onDayReminder = long("due_date_reminder").nullable()
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
    var rrule by TaskTable.rrule
    var onDayReminder by TaskTable.onDayReminder
    var completed by TaskTable.completed
    var priority by TaskTable.priority
    var createdAt by TaskTable.createdAt
    var editedAt by TaskTable.editedAt
    var completedAt by TaskTable.completedAt

    val subTasks by SubTaskEntity referrersOn SubTaskTable.task
    val userEntity by UserEntity referencedOn TaskTable.user
    val itemEntity by ItemEntity optionalReferencedOn TaskTable.item
}

fun TaskEntity.fromData(taskData: TaskData) {
    user = taskData.userId.toEntityId(UsersTable)
    item = taskData.itemId?.toEntityId(ItemTable)
    name = taskData.name
    description = taskData.description
    dueDate = taskData.dueDate
    rrule = taskData.rrule
    onDayReminder = taskData.onDayReminder
    completed = taskData.completed
    priority = taskData.priority
    createdAt = taskData.createdAt
    editedAt = taskData.editedAt
    completedAt = taskData.completedAt
}

fun TaskEntity.toData() = TaskData(
    id = id.toIxId(),
    userId = user.toIxId(),
    itemId = item?.toIxId(),
    name = name,
    description = description,
    dueDate = dueDate,
    rrule = rrule,
    onDayReminder = onDayReminder,
    completed = completed,
    priority = priority,
    createdAt = createdAt,
    editedAt = editedAt,
    completedAt = completedAt,
    subTasks = subTasks.map { it.toData() },
)