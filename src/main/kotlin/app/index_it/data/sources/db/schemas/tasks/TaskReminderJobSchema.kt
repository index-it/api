package app.index_it.data.sources.db.schemas.tasks

import app.index_it.data.models.tasks.TaskReminderJobDto
import app.index_it.data.sources.db.schemas.user.UserEntity
import app.index_it.data.sources.db.schemas.user.UsersTable
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*

/**
 * Table that stores the task reminder jobs
 *
 * @property id job id
 * @property task task id
 */
object TaskReminderJobTable : UUIDTable() {
    val task = reference(
        name = "id_task",
        foreign = TaskTable,
        onDelete = ReferenceOption.CASCADE
    ).index()
    val user = reference(
        name = "id_user",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE
    ).index()
}

class TaskReminderJobEntity(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<TaskReminderJobEntity>(TaskReminderJobTable)

    var task by TaskReminderJobTable.task
    var user by TaskReminderJobTable.user

    val taskEntity by TaskEntity referencedOn TaskReminderJobTable.task
    val userEntity by UserEntity referencedOn TaskReminderJobTable.user
}

fun TaskReminderJobEntity.toDto() = TaskReminderJobDto(
    id = id.toIxId(),
    task = taskEntity.toDto(),
    userId = user.toIxId()
)