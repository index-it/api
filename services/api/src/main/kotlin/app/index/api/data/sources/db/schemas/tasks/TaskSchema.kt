package app.index.api.data.sources.db.schemas.tasks

import app.index.api.data.models.tasks.TaskData
import app.index.api.data.sources.db.schemas.lists.ItemEntity
import app.index.api.data.sources.db.schemas.lists.ItemTable
import app.index.api.data.sources.db.schemas.tasks.TaskTable.completed
import app.index.api.data.sources.db.schemas.tasks.TaskTable.completed_at
import app.index.api.data.sources.db.schemas.tasks.TaskTable.created_at
import app.index.api.data.sources.db.schemas.tasks.TaskTable.description
import app.index.api.data.sources.db.schemas.tasks.TaskTable.due_date
import app.index.api.data.sources.db.schemas.tasks.TaskTable.edited_at
import app.index.api.data.sources.db.schemas.tasks.TaskTable.item
import app.index.api.data.sources.db.schemas.tasks.TaskTable.name
import app.index.api.data.sources.db.schemas.tasks.TaskTable.priority
import app.index.api.data.sources.db.schemas.tasks.TaskTable.rrule
import app.index.api.data.sources.db.schemas.tasks.TaskTable.user
import app.index.api.data.sources.db.schemas.user.UserEntity
import app.index.api.data.sources.db.schemas.user.UsersTable
import app.index.api.data.sources.db.toEntityId
import app.index.api.data.sources.db.toIxId
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

/**
 * @property id
 * @property user
 * @property item
 * @property name
 * @property description
 * @property due_date
 * @property rrule
 * @property completed
 * @property priority
 * @property created_at
 * @property edited_at
 * @property completed_at
 */
object TaskTable : UUIDTable() {
    val user = reference(
        name = "id_user",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE,
    )
    val item = reference(
        name = "id_item",
        foreign = ItemTable,
        onDelete = ReferenceOption.SET_NULL,
    ).nullable()
    val name = varchar("ix_name", 150)
    val description = varchar("description", 500).nullable()
    val due_date = date("due_date").nullable()
    val rrule = varchar("rrule", 200).nullable()
    val completed = bool("completed")
    val priority = integer("priority").nullable()
    val created_at = timestamp("created_at")
    val edited_at = timestamp("edited_at").nullable()
    val completed_at = timestamp("completed_at").nullable()
}

/**
 * @property id
 * @property user
 * @property item
 * @property name
 * @property description
 * @property due_date
 * @property completed
 * @property priority
 * @property created_at
 * @property edited_at
 * @property completed_at
 *
 * @property subTasks
 * @property reminders
 *
 * @property userEntity
 * @property itemEntity
 */
class TaskEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<TaskEntity>(TaskTable)

    var user by TaskTable.user
    var item by TaskTable.item
    var name by TaskTable.name
    var description by TaskTable.description
    var due_date by TaskTable.due_date
    var rrule by TaskTable.rrule
    var completed by TaskTable.completed
    var priority by TaskTable.priority
    var created_at by TaskTable.created_at
    var edited_at by TaskTable.edited_at
    var completed_at by TaskTable.completed_at

    val subTasks by SubTaskEntity referrersOn SubTaskTable.task
    val reminders by TaskReminderEntity referrersOn TaskReminderTable.task
    @Suppress("MemberVisibilityCanBePrivate")
    val userEntity by UserEntity referencedOn TaskTable.user
    @Suppress("MemberVisibilityCanBePrivate")
    val itemEntity by ItemEntity optionalReferencedOn TaskTable.item
}

fun TaskEntity.fromData(taskData: TaskData) {
    user = taskData.user_id.toEntityId(UsersTable)
    item = taskData.item_id?.toEntityId(ItemTable)
    name = taskData.name
    description = taskData.description
    due_date = taskData.due_date?.toJavaLocalDate()
    rrule = taskData.rrule
    completed = taskData.completed
    priority = taskData.priority
    created_at = Instant.ofEpochMilli(taskData.created_at)
    edited_at = taskData.edited_at?.let { Instant.ofEpochMilli(it) }
    completed_at = taskData.completed_at?.let { Instant.ofEpochMilli(it) }
}

fun TaskEntity.toData() = TaskData(
    id = id.toIxId(),
    user_id = user.toIxId(),
    item_id = item?.toIxId(),
    name = name,
    description = description,
    due_date = due_date?.toKotlinLocalDate(),
    rrule = rrule,
    completed = completed,
    priority = priority,
    reminders = reminders.map { it.toData() },
    created_at = created_at.toEpochMilli(),
    edited_at = edited_at?.toEpochMilli(),
    completed_at = completed_at?.toEpochMilli(),
    subtasks = subTasks.map { it.toData() },
)