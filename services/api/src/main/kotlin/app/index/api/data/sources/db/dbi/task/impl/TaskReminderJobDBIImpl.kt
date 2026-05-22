package app.index.api.data.sources.db.dbi.task.impl

import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.tasks.TaskData
import app.index.api.data.models.tasks.TaskReminderJobData
import app.index.api.data.sources.db.dbi.task.TaskReminderJobDBI
import app.index.api.data.sources.db.schemas.tasks.TaskReminderJobEntity
import app.index.api.data.sources.db.schemas.tasks.TaskReminderJobTable
import app.index.api.data.sources.db.schemas.tasks.TaskTable
import app.index.api.data.sources.db.schemas.tasks.toData
import app.index.api.data.sources.db.schemas.user.UsersTable
import app.index.api.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.updateReturning
import org.koin.core.annotation.Single
import java.time.Instant

@Single(createdAtStart = true)
class TaskReminderJobDBIImpl : TaskReminderJobDBI {
    override suspend fun create(
        taskReminderJobCreateData: TaskReminderJobData.TaskReminderJobCreateData
    ) {
        dbQuery {
            TaskReminderJobEntity.new(taskReminderJobCreateData.id.id) {
                task = taskReminderJobCreateData.taskId.toEntityId(TaskTable)
                user = taskReminderJobCreateData.userId.toEntityId(UsersTable)
                scheduledAt = Instant.ofEpochMilli(taskReminderJobCreateData.scheduledAt)
                rescheduleCount = 0
            }
        }
    }

    override suspend fun createAll(taskReminderJobCreateData: List<TaskReminderJobData.TaskReminderJobCreateData>) {
        dbQuery {
            TaskReminderJobTable.batchInsert(taskReminderJobCreateData) {
                this[TaskReminderJobTable.id] = it.id.toEntityId(TaskReminderJobTable)
                this[TaskReminderJobTable.task] = it.taskId.toEntityId(TaskTable)
                this[TaskReminderJobTable.user] = it.userId.toEntityId(UsersTable)
                this[TaskReminderJobTable.scheduledAt] = Instant.ofEpochMilli(it.scheduledAt)
                this[TaskReminderJobTable.rescheduleCount] = 0
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
                .map { it.toData() }
        }


    override suspend fun increaseRescheduleCount(id: IxId<TaskReminderJobData>): TaskReminderJobData? =
        dbQuery {
            TaskReminderJobTable.updateReturning(where = { TaskReminderJobTable.id eq id.toEntityId(TaskReminderJobTable) }) {
                it[this.rescheduleCount] = this.rescheduleCount + 1
            }
                .firstOrNull()
                ?.let { TaskReminderJobEntity.wrapRow(it).toData() }
        }

    override suspend fun delete(jobId: IxId<TaskReminderJobData>) {
        dbQuery {
            TaskReminderJobTable.deleteWhere {
                id eq jobId.toEntityId(TaskReminderJobTable)
            }
        }
    }

    override suspend fun deleteMultiple(jobIds: List<IxId<TaskReminderJobData>>) {
        dbQuery {
            val ids = jobIds.map { it.toEntityId(TaskReminderJobTable) }

            TaskReminderJobTable.deleteWhere {
                id inList ids
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
