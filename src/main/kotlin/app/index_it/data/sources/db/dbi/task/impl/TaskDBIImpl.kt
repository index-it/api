package app.index_it.data.sources.db.dbi.task.impl

import app.index_it.core.logic.currentMillis
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.task.TaskDBI
import app.index_it.data.sources.db.schemas.lists.ItemTable
import app.index_it.data.sources.db.schemas.tasks.TaskEntity
import app.index_it.data.sources.db.schemas.tasks.TaskTable
import app.index_it.data.sources.db.schemas.user.UserTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
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

    private fun userAndTaskFilter(userId: IxId<UserDto>, taskId: IxId<TaskDto>) = Op.build { (TaskTable.user eq userId.toEntityId(UserTable)) and (TaskTable.id eq taskId.toEntityId(TaskTable)) }

    override suspend fun exists(userId: IxId<UserDto>, taskId: IxId<TaskDto>): Boolean {
        return get(userId, taskId) != null
    }

    override suspend fun create(taskDto: TaskDto) {
        dbQuery {
            TaskEntity.new(taskDto.id.id) {
                fromDto(taskDto)
            }
        }
    }

    override suspend fun get(userId: IxId<UserDto>): List<TaskDto> = dbQuery {
        TaskEntity
            .find { TaskTable.user eq userId.toEntityId(UserTable) }
            .map { it.toDto() }
    }

    override suspend fun get(userId: IxId<UserDto>, taskId: IxId<TaskDto>): TaskDto? = dbQuery {
        TaskEntity
            .find { userAndTaskFilter(userId, taskId) }
            .limit(1)
            .firstOrNull()
            ?.toDto()
    }

    override suspend fun setCompletion(userId: IxId<UserDto>, taskId: IxId<TaskDto>, completed: Boolean): Boolean = dbQuery {
        TaskTable.update({ userAndTaskFilter(userId, taskId) }) {
            it[this.completed] = completed
            it[this.completedAt] = if (completed) currentMillis() else null
        } > 0
    }

    override suspend fun setLinking(userId: IxId<UserDto>, taskId: IxId<TaskDto>, itemId: IxId<ItemDto>?): Boolean = dbQuery {
        TaskTable.update({ userAndTaskFilter(userId, taskId) }) {
            it[this.item] = itemId?.toEntityId(ItemTable)
        } > 0
    }


    override suspend fun update(userId: IxId<UserDto>, taskId: IxId<TaskDto>, taskUpdateRequestDto: TaskDto.TaskUpdateRequestDto): Boolean = dbQuery {
        TaskTable.update({ userAndTaskFilter(userId, taskId) }) {
            it[name] = taskUpdateRequestDto.name
            it[description] = taskUpdateRequestDto.description
            it[dueDate] = taskUpdateRequestDto.dueDate
            it[priority] = taskUpdateRequestDto.priority
            it[editedAt] = currentMillis()
        } > 0
    }

    override suspend fun delete(userId: IxId<UserDto>, taskId: IxId<TaskDto>) {
        dbQuery {
            TaskTable.deleteWhere { userAndTaskFilter(userId, taskId) }
        }
    }
}