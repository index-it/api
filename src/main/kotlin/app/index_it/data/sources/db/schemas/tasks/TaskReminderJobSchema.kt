package app.index_it.data.sources.db.schemas.tasks

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
}

class TaskReminderJobEntity(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<TaskReminderJobEntity>(TaskReminderJobTable)

    var task by TaskReminderJobTable.task

    val taskEntity by TaskEntity referencedOn TaskReminderJobTable.task
}