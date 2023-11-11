package app.index_it.data.sources.db.dbi.task.impl

import app.index_it.core.logic.currentMillis
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.task.TaskDBI
import app.index_it.data.sources.db.schemas.lists.ItemTable
import app.index_it.data.sources.db.schemas.tasks.TaskEntity
import app.index_it.data.sources.db.schemas.tasks.TaskTable
import app.index_it.data.sources.db.schemas.user.UserTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

object TaskDBIImpl : TaskDBI {
    private fun TaskEntity.fromDto(taskDto: TaskDto) {
        user = taskDto.userId.toEntityId(UserTable)
        item = taskDto.itemId?.toEntityId(ItemTable)
        name = taskDto.name
        description = taskDto.description
        dueDate = taskDto.dueDate
        completed = taskDto.completed
        priority = taskDto.priority
        createdAt = taskDto.createdAt
        editedAt = taskDto.editedAt
        completedAt = taskDto.completedAt
    }

    private fun TaskEntity.toDto() = TaskDto(
        id = id.toIxId(),
        userId = user.toIxId(),
        itemId = item?.toIxId(),
        name = name,
        description = description,
        dueDate = dueDate,
        completed = completed,
        priority = priority,
        createdAt = createdAt,
        editedAt = editedAt,
        completedAt = completedAt
    )

    override suspend fun exists(id: IxId<TaskDto>): Boolean {
        return dbQuery {
            TaskEntity.findById(id.id) != null
        }
    }

    override suspend fun create(taskDto: TaskDto) {
        dbQuery {
            TaskEntity.new(taskDto.id.id) {
                fromDto(taskDto)
            }
        }
    }

    override suspend fun get(id: IxId<UserDto>): List<TaskDto> = dbQuery {
        TaskEntity
            .find { TaskTable.user eq id.toEntityId(UserTable) }
            .map { it.toDto() }
    }

    override suspend fun get(id: IxId<TaskDto>): TaskDto? = dbQuery {
        TaskEntity.findById(id.id)?.toDto()
    }

    override suspend fun setCompletion(id: IxId<TaskDto>, completed: Boolean) {
        dbQuery {
            TaskTable.update({ TaskTable.id eq id.toEntityId(TaskTable) }) {
                it[this.completed] = completed
                it[this.completedAt] = if (completed) currentMillis() else null
            }
        }
    }

    override suspend fun setLinking(taskId: IxId<TaskDto>, itemId: IxId<TaskDto>) {
        dbQuery {
            TaskTable.update({ TaskTable.id eq taskId.toEntityId(TaskTable) }) {
                it[this.item] = itemId.toEntityId(ItemTable)
            }
        }
    }

    override suspend fun update(id: IxId<TaskDto>, taskUpdateRequestDto: TaskDto.TaskUpdateRequestDto) {
        dbQuery {
            TaskTable.update({ TaskTable.id eq id.toEntityId(TaskTable) }) {
                it[name] = taskUpdateRequestDto.name
                it[description] = taskUpdateRequestDto.description
                it[dueDate] = taskUpdateRequestDto.dueDate
                it[priority] = taskUpdateRequestDto.priority
                it[editedAt] = currentMillis()
            }
        }
    }

    override suspend fun delete(id: IxId<TaskDto>) {
        dbQuery {
            TaskTable.deleteWhere { TaskTable.id eq id.toEntityId(TaskTable) }
        }
    }
}