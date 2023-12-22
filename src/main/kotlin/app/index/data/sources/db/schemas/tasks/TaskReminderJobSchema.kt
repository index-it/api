package app.index.data.sources.db.schemas.tasks

import app.index.data.models.tasks.TaskReminderJobData
import app.index.data.sources.db.schemas.tasks.TaskReminderJobTable.id
import app.index.data.sources.db.schemas.tasks.TaskReminderJobTable.task
import app.index.data.sources.db.schemas.user.UserEntity
import app.index.data.sources.db.schemas.user.UsersTable
import app.index.data.sources.db.toIxId
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*

/**
 * Table that stores the task reminder jobs
 *
 * @property id
 * @property task
 * @property user
 * @property scheduledAt
 */
object TaskReminderJobTable : UUIDTable() {
    val task = reference(
        name = "id_task",
        foreign = TaskTable,
        onDelete = ReferenceOption.CASCADE,
    ).index()
    val user = reference(
        name = "id_user",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE,
    ).index()
    val scheduledAt = long("scheduled_at")
}

/**
 * @property task
 * @property user
 * @property scheduledAt
 *
 * @property taskEntity
 * @property userEntity
 */
class TaskReminderJobEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<TaskReminderJobEntity>(TaskReminderJobTable)

    var task by TaskReminderJobTable.task
    var user by TaskReminderJobTable.user
    var scheduledAt by TaskReminderJobTable.scheduledAt

    val taskEntity by TaskEntity referencedOn TaskReminderJobTable.task
    @Suppress("MemberVisibilityCanBePrivate")
    val userEntity by UserEntity referencedOn TaskReminderJobTable.user
}

fun TaskReminderJobEntity.toData() = TaskReminderJobData(
    id = id.toIxId(),
    task = taskEntity.toData(),
    userId = user.toIxId(),
    scheduledAt = scheduledAt
)
