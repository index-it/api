package app.index.data.sources.db.dbi.task.impl

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.tasks.TaskData
import app.index.data.models.tasks.TaskReminderJobDto
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.task.TaskReminderJobDBI
import app.index.data.sources.db.schemas.tasks.TaskReminderJobEntity
import app.index.data.sources.db.schemas.tasks.TaskReminderJobTable
import app.index.data.sources.db.schemas.tasks.TaskTable
import app.index.data.sources.db.schemas.tasks.toData
import app.index.data.sources.db.schemas.user.UsersTable
import app.index.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class TaskReminderJobDBIImpl : TaskReminderJobDBI {
    override suspend fun create(
        jobId: IxId<TaskReminderJobDto>,
        taskId: IxId<TaskData>,
        userId: IxId<UserData>,
    ) {
        dbQuery {
            TaskReminderJobEntity.new(jobId.id) {
                task = taskId.toEntityId(TaskTable)
                user = userId.toEntityId(UsersTable)
            }
        }
    }

    override suspend fun get(jobId: IxId<TaskReminderJobDto>): TaskReminderJobDto? =
        dbQuery {
            TaskReminderJobEntity
                .find { TaskReminderJobTable.id eq jobId.toEntityId(TaskReminderJobTable) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun getOfTask(taskId: IxId<TaskData>): TaskReminderJobDto? =
        dbQuery {
            TaskReminderJobEntity
                .find { TaskReminderJobTable.task eq taskId.toEntityId(TaskTable) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun delete(jobId: IxId<TaskReminderJobDto>) {
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
