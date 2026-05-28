package app.index.shared.core.data.sources.db.schemas.tasks

import app.index.shared.core.data.models.tasks.TaskReminderData
import app.index.shared.core.data.sources.db.schemas.tasks.TaskReminderTable.days_before
import app.index.shared.core.data.sources.db.schemas.tasks.TaskReminderTable.task
import app.index.shared.core.data.sources.db.schemas.tasks.TaskReminderTable.time_offset
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * @property id
 * @property task
 * @property days_before
 * @property time_offset
 */
object TaskReminderTable : IntIdTable() {
    val task = reference(
        name = "id_task",
        foreign = TaskTable,
        onDelete = ReferenceOption.CASCADE,
    ).index()
    val days_before = integer("days_before")
    val time_offset = long("time_offset")
}

/**
 * @property id
 * @property task
 * @property days_before
 * @property time_offset
 *
 * @property taskEntity
 */
class TaskReminderEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TaskReminderEntity>(TaskReminderTable)

    var task by TaskReminderTable.task
    var days_before by TaskReminderTable.days_before
    var time_offset by TaskReminderTable.time_offset

    @Suppress("MemberVisibilityCanBePrivate")
    val taskEntity by TaskEntity referencedOn TaskReminderTable.task
}

fun TaskReminderEntity.toData() = TaskReminderData(
    days_before = days_before,
    time_offset = time_offset
)