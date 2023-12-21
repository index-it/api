package app.index.data.sources.db.dbi.task.impl

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.tasks.TaskData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.task.TaskDBI
import app.index.data.sources.db.schemas.lists.ItemTable
import app.index.data.sources.db.schemas.tasks.*
import app.index.data.sources.db.schemas.user.UsersTable
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class TaskDBIImpl : TaskDBI {
    private fun userAndTaskFilter(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
    ) = Op.build { (TaskTable.user eq userId.toEntityId(UsersTable)) and (TaskTable.id eq taskId.toEntityId(TaskTable)) }

    override suspend fun exists(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
    ): Boolean {
        return get(userId, taskId) != null
    }

    override suspend fun create(taskData: TaskData) {
        dbQuery {
            TaskEntity.new(taskData.id.id) {
                fromData(taskData)
            }

            SubTaskTable.batchInsert(taskData.subTasks) {
                this[SubTaskTable.task] = taskData.id.toEntityId(TaskTable)
                this[SubTaskTable.name] = it.name
                this[SubTaskTable.completed] = it.completed
            }
        }
    }

    override suspend fun get(userId: IxId<UserData>): List<TaskData> =
        dbQuery {
            TaskEntity
                .find { TaskTable.user eq userId.toEntityId(UsersTable) }
                .map { it.toData() }
        }

    override suspend fun get(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
    ): TaskData? =
        dbQuery {
            TaskEntity
                .find { userAndTaskFilter(userId, taskId) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun setCompletion(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
        completed: Boolean,
    ): Boolean =
        dbQuery {
            TaskTable.update({ userAndTaskFilter(userId, taskId) }) {
                it[this.completed] = completed
                it[this.completedAt] = if (completed) DatetimeUtils.currentMillis() else null
            } > 0
        }

    override suspend fun update(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
        taskUpdateRequestData: TaskData.TaskUpdateRequestData,
    ): Boolean =
        dbQuery {
            SubTaskTable.deleteWhere { task eq taskId.toEntityId(TaskTable) }
            SubTaskTable.batchInsert(taskUpdateRequestData.subTasks) {
                this[SubTaskTable.task] = taskId.toEntityId(TaskTable)
                this[SubTaskTable.name] = it.name
                this[SubTaskTable.completed] = it.completed
            }

            TaskTable.update({ userAndTaskFilter(userId, taskId) }) {
                it[name] = taskUpdateRequestData.name
                it[description] = taskUpdateRequestData.description
                it[dueDate] = taskUpdateRequestData.dueDate
                it[rrule] = taskUpdateRequestData.rrule
                it[onDayReminder] = taskUpdateRequestData.onDayReminder
                it[priority] = taskUpdateRequestData.priority
                it[editedAt] = DatetimeUtils.currentMillis()
                it[item] = taskUpdateRequestData.itemId?.toEntityId(ItemTable)
            } > 0
        }

    override suspend fun delete(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
    ) {
        dbQuery {
            TaskTable.deleteWhere { userAndTaskFilter(userId, taskId) }
        }
    }
}
