package app.index.data.sources.db.schemas.tasks

import app.index.data.models.tasks.TaskReminderData
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * @property id
 * @property task
 * @property daysBefore
 * @property timeOffset
 */
object TaskReminderTable : IntIdTable() {
    val task = reference(
        name = "id_task",
        foreign = TaskTable,
        onDelete = ReferenceOption.CASCADE,
    ).index()
    val daysBefore = integer("days_before")
    val timeOffset = long("time_offset")
}

class TaskReminderEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TaskReminderEntity>(TaskReminderTable)

    var task by TaskReminderTable.task
    var daysBefore by TaskReminderTable.daysBefore
    var timeOffset by TaskReminderTable.timeOffset

    val taskEntity by TaskEntity referencedOn TaskReminderTable.task
}

fun TaskReminderEntity.toData() = TaskReminderData(
    daysBefore = daysBefore,
    timeOffset = timeOffset
)