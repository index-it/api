package app.index_it.data.sources.db.dbi.task.impl

import app.index_it.core.logic.DatetimeUtils
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.tasks.SubTaskDto
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.task.TaskDBI
import app.index_it.data.sources.db.schemas.lists.ItemTable
import app.index_it.data.sources.db.schemas.tasks.SubTaskEntity
import app.index_it.data.sources.db.schemas.tasks.SubTaskTable
import app.index_it.data.sources.db.schemas.tasks.TaskEntity
import app.index_it.data.sources.db.schemas.tasks.TaskTable
import app.index_it.data.sources.db.schemas.user.UsersTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object TaskDBIImpl : TaskDBI {
    private fun TaskEntity.fromDto(taskDto: TaskDto) {
        user = taskDto.userId.toEntityId(UsersTable)
        item = taskDto.itemId?.toEntityId(ItemTable)
        name = taskDto.name
        description = taskDto.description
        dueDate = taskDto.dueDate
        rrule = taskDto.rrule
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
        rrule = rrule,
        completed = completed,
        priority = priority,
        createdAt = createdAt,
        editedAt = editedAt,
        completedAt = completedAt,
        subTasks = subTasks.map { it.toDto() }
    )

    /*
    private fun SubTaskEntity.fromDto(subTaskDto: SubTaskDto) {
        name = subTaskDto.name
        completed = subTaskDto.completed
    }
     */

    private fun SubTaskEntity.toDto() = SubTaskDto(
        name = name,
        completed = completed
    )

    private fun userAndTaskFilter(userId: IxId<UserDto>, taskId: IxId<TaskDto>) = Op.build { (TaskTable.user eq userId.toEntityId(UsersTable)) and (TaskTable.id eq taskId.toEntityId(TaskTable)) }

    override suspend fun exists(userId: IxId<UserDto>, taskId: IxId<TaskDto>): Boolean {
        return get(userId, taskId) != null
    }

    override suspend fun create(taskDto: TaskDto) {
        dbQuery {
            TaskEntity.new(taskDto.id.id) {
                fromDto(taskDto)
            }

            SubTaskTable.batchInsert(taskDto.subTasks) {
                this[SubTaskTable.task] = taskDto.id.toEntityId(TaskTable)
                this[SubTaskTable.name] = it.name
                this[SubTaskTable.completed] = it.completed
            }
        }
    }

    override suspend fun get(userId: IxId<UserDto>): List<TaskDto> = dbQuery {
        TaskEntity
            .find { TaskTable.user eq userId.toEntityId(UsersTable) }
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
            it[this.completedAt] = if (completed) DatetimeUtils.currentMillis() else null
        } > 0
    }

    override suspend fun setItemConnection(userId: IxId<UserDto>, taskId: IxId<TaskDto>, itemId: IxId<ItemDto>?): Boolean = dbQuery {
        TaskTable.update({ userAndTaskFilter(userId, taskId) }) {
            it[this.item] = itemId?.toEntityId(ItemTable)
        } > 0
    }


    override suspend fun update(userId: IxId<UserDto>, taskId: IxId<TaskDto>, taskUpdateRequestDto: TaskDto.TaskUpdateRequestDto): Boolean = dbQuery {
        SubTaskTable.deleteWhere { SubTaskTable.task eq taskId.toEntityId(TaskTable) }
        SubTaskTable.batchInsert(taskUpdateRequestDto.subTasks) {
            this[SubTaskTable.task] = taskId.toEntityId(TaskTable)
            this[SubTaskTable.name] = it.name
            this[SubTaskTable.completed] = it.completed
        }

        TaskTable.update({ userAndTaskFilter(userId, taskId) }) {
            it[name] = taskUpdateRequestDto.name
            it[description] = taskUpdateRequestDto.description
            it[dueDate] = taskUpdateRequestDto.dueDate
            it[rrule] = taskUpdateRequestDto.rrule
            it[priority] = taskUpdateRequestDto.priority
            it[editedAt] = DatetimeUtils.currentMillis()
        } > 0
    }

    override suspend fun delete(userId: IxId<UserDto>, taskId: IxId<TaskDto>) {
        dbQuery {
            TaskTable.deleteWhere { userAndTaskFilter(userId, taskId) }
        }
    }
}