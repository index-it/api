package app.index.data.sources.db.dbi.task.impl

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.tasks.TaskData
import app.index.data.models.tasks.TaskReminderData
import app.index.data.models.tasks.TaskReminderJobData
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.task.TaskReminderJobDBI
import app.index.data.sources.db.schemas.tasks.TaskReminderJobEntity
import app.index.data.sources.db.schemas.tasks.TaskReminderJobTable
import app.index.data.sources.db.schemas.tasks.TaskTable
import app.index.data.sources.db.schemas.tasks.toData
import app.index.data.sources.db.schemas.user.UsersTable
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class TaskReminderJobDBIImpl : TaskReminderJobDBI {
    override suspend fun create(
        taskReminderJobCreateData: TaskReminderJobData.TaskReminderJobCreateData
    ) {
        dbQuery {
            TaskReminderJobEntity.new(taskReminderJobCreateData.id.id) {
                task = taskReminderJobCreateData.taskId.toEntityId(TaskTable)
                user = taskReminderJobCreateData.userId.toEntityId(UsersTable)
                scheduledAt = taskReminderJobCreateData.scheduledAt
            }
        }
    }

    override suspend fun createAll(taskReminderJobCreateData: List<TaskReminderJobData.TaskReminderJobCreateData>) {
        dbQuery {
            TaskReminderJobTable.batchInsert(taskReminderJobCreateData) {
                this[TaskReminderJobTable.id] = it.id.toEntityId(TaskReminderJobTable)
                this[TaskReminderJobTable.task] = it.taskId.toEntityId(TaskTable)
                this[TaskReminderJobTable.user] = it.userId.toEntityId(UsersTable)
                this[TaskReminderJobTable.scheduledAt] = it.scheduledAt
            }
        }
    }

    override suspend fun get(jobId: IxId<TaskReminderJobData>): TaskReminderJobData? =
        dbQuery {
            TaskReminderJobEntity
                .find { TaskReminderJobTable.id eq jobId.toEntityId(TaskReminderJobTable) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun getAllOfTask(taskId: IxId<TaskData>): List<TaskReminderJobData> =
        dbQuery {
            TaskReminderJobEntity
                .find { TaskReminderJobTable.task eq taskId.toEntityId(TaskTable) }
                .limit(1)
                .map { it.toData() }
        }

    override suspend fun delete(jobId: IxId<TaskReminderJobData>) {
        dbQuery {
            TaskReminderJobTable.deleteWhere {
                id eq jobId.toEntityId(TaskReminderJobTable)
            }
        }
    }

    override suspend fun deleteAllOfTask(taskId: IxId<TaskData>) {
        dbQuery {
            TaskReminderJobTable.deleteWhere {
                task eq taskId.toEntityId(TaskTable)
            }
        }
    }
}
