package app.index_it.data.sources.db.dbi.task.impl

import app.index_it.core.logic.DatetimeUtils
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.task.TaskDBI
import app.index_it.data.sources.db.schemas.lists.ItemTable
import app.index_it.data.sources.db.schemas.tasks.*
import app.index_it.data.sources.db.schemas.user.UsersTable
import app.index_it.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class TaskDBIImpl : TaskDBI {
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
            it[onDayReminder] = taskUpdateRequestDto.onDayReminder
            it[priority] = taskUpdateRequestDto.priority
            it[editedAt] = DatetimeUtils.currentMillis()
            it[item] = taskUpdateRequestDto.itemId?.toEntityId(ItemTable)
        } > 0
    }

    override suspend fun delete(userId: IxId<UserDto>, taskId: IxId<TaskDto>) {
        dbQuery {
            TaskTable.deleteWhere { userAndTaskFilter(userId, taskId) }
        }
    }
}