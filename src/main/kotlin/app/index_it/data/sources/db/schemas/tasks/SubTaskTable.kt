package app.index_it.data.sources.db.schemas.tasks

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object SubTaskTable : IntIdTable() {
    val task = reference(
        name = "id_task",
        foreign = TaskTable,
        onDelete = ReferenceOption.CASCADE
    )
    val name = varchar("ix_name", 150)
    val completed = bool("completed")
}

class SubTaskEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<SubTaskEntity>(SubTaskTable)

    var task by SubTaskTable.task
    var name by SubTaskTable.name
    var completed by SubTaskTable.completed

    val taskEntity by TaskEntity referencedOn SubTaskTable.task
}