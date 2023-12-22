package app.index.data.sources.db.schemas.tasks

import app.index.data.models.tasks.SubTaskData
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object SubTaskTable : IntIdTable() {
    val task = reference(
        name = "id_task",
        foreign = TaskTable,
        onDelete = ReferenceOption.CASCADE,
    ).index()
    val name = varchar("ix_name", 150)
    val completed = bool("completed")
}

class SubTaskEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SubTaskEntity>(SubTaskTable)

    var task by SubTaskTable.task
    var name by SubTaskTable.name
    var completed by SubTaskTable.completed

    @Suppress("UNUSED")
    val taskEntity by TaskEntity referencedOn SubTaskTable.task
}

fun SubTaskEntity.toData() = SubTaskData(
    name = name,
    completed = completed,
)