package app.index.api.data.sources.db.dbi.task.impl

import app.index.shared.core.logic.DatetimeUtils
import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.lists.ItemData
import app.index.shared.core.data.models.tasks.TaskData
import app.index.shared.core.data.models.user.UserData
import app.index.api.data.sources.db.dbi.task.TaskDBI
import app.index.api.data.sources.db.schemas.lists.ItemTable
import app.index.api.data.sources.db.schemas.tasks.*
import app.index.api.data.sources.db.schemas.user.UsersTable
import app.index.api.data.sources.db.toEntityId
import kotlinx.datetime.toJavaLocalDate
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

            SubTaskTable.batchInsert(taskData.subtasks) {
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

    override suspend fun getUncompleted(userId: IxId<UserData>): List<TaskData> =
        dbQuery {
            TaskEntity
                .find { (TaskTable.user eq userId.toEntityId(UsersTable)) and (TaskTable.completed eq false) }
                .map { it.toData() }
        }

    override suspend fun getCompleted(userId: IxId<UserData>): List<TaskData> =
        dbQuery {
            TaskEntity
                .find { (TaskTable.user eq userId.toEntityId(UsersTable)) and (TaskTable.completed eq true) }
                .map { it.toData() }
        }

    override suspend fun getConnectedToItem(itemId: IxId<ItemData>): List<TaskData> =
        dbQuery {
            TaskEntity
                .find { TaskTable.item eq itemId.toEntityId(ItemTable) }
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
    ): TaskData? =
        dbQuery {
            TaskTable.updateReturning(where = { userAndTaskFilter(userId, taskId) }) {
                it[this.completed] = completed
                it[this.completed_at] = if (completed) DatetimeUtils.currentJavaInstant() else null
            }.firstOrNull()?.let {
                TaskEntity.wrapRow(it).toData()
            }
        }

    override suspend fun setCompletionOfAllTasksConnectedToItem(
        itemId: IxId<ItemData>,
        completed: Boolean
    ): List<TaskData> =
        dbQuery {
            TaskTable.updateReturning(where = { TaskTable.item eq itemId.toEntityId(ItemTable) }) {
                it[this.completed] = completed
                it[this.completed_at] = if (completed) DatetimeUtils.currentJavaInstant() else null
            }.map {
                TaskEntity.wrapRow(it).toData()
            }
        }


    override suspend fun update(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
        taskUpdateRequestData: TaskData.TaskUpdateRequestData,
    ): Boolean =
        dbQuery {
            val exists = TaskTable.selectAll().where { userAndTaskFilter(userId, taskId) }.limit(1).firstOrNull() != null

            if (exists) {
                TaskTable.update({ userAndTaskFilter(userId, taskId) }) {
                    it[name] = taskUpdateRequestData.name
                    it[description] = taskUpdateRequestData.description
                    it[due_date] = taskUpdateRequestData.due_date?.toJavaLocalDate()
                    it[rrule] = taskUpdateRequestData.rrule
                    it[priority] = taskUpdateRequestData.priority
                    it[edited_at] = DatetimeUtils.currentJavaInstant()
                    it[item] = taskUpdateRequestData.item_id?.toEntityId(ItemTable)
                } > 0

                SubTaskTable.deleteWhere { task eq taskId.toEntityId(TaskTable) }
                SubTaskTable.batchInsert(taskUpdateRequestData.subtasks) {
                    this[SubTaskTable.task] = taskId.toEntityId(TaskTable)
                    this[SubTaskTable.name] = it.name
                    this[SubTaskTable.completed] = it.completed
                }

                TaskReminderTable.deleteWhere { task eq taskId.toEntityId(TaskTable) }
                TaskReminderTable.batchInsert(taskUpdateRequestData.reminders) {
                    this[TaskReminderTable.task] = taskId.toEntityId(TaskTable)
                    this[TaskReminderTable.days_before] = it.days_before
                    this[TaskReminderTable.time_offset] = it.time_offset
                }
            }

            return@dbQuery exists
        }

    override suspend fun delete(
        userId: IxId<UserData>,
        taskId: IxId<TaskData>,
    ) : Boolean = dbQuery {
        TaskTable.deleteWhere { userAndTaskFilter(userId, taskId) } > 0
    }
}
