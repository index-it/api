package app.index_it.data.sources.db.dbi.task.impl

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.tasks.TaskReminderJobDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.task.TaskReminderJobDBI
import app.index_it.data.sources.db.schemas.tasks.TaskReminderJobEntity
import app.index_it.data.sources.db.schemas.tasks.TaskReminderJobTable
import app.index_it.data.sources.db.schemas.tasks.TaskTable
import app.index_it.data.sources.db.schemas.tasks.toDto
import app.index_it.data.sources.db.schemas.user.UsersTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class TaskReminderJobDBIImpl : TaskReminderJobDBI {
    override suspend fun create(jobId: IxId<TaskReminderJobDto>, taskId: IxId<TaskDto>, userId: IxId<UserDto>) {
        dbQuery {
            TaskReminderJobEntity.new(jobId.id) {
                task = taskId.toEntityId(TaskTable)
                user = userId.toEntityId(UsersTable)
            }
        }
    }

    override suspend fun get(jobId: IxId<TaskReminderJobDto>): TaskReminderJobDto? = dbQuery {
        TaskReminderJobEntity
            .find { TaskReminderJobTable.id eq jobId.toEntityId(TaskReminderJobTable) }
            .limit(1)
            .firstOrNull()
            ?.toDto()
    }

    override suspend fun getFromTask(taskId: IxId<TaskDto>): TaskReminderJobDto? = dbQuery {
        TaskReminderJobEntity
            .find { TaskReminderJobTable.task eq taskId.toEntityId(TaskTable) }
            .limit(1)
            .firstOrNull()
            ?.toDto()
    }

    override suspend fun delete(jobId: IxId<TaskReminderJobDto>) {
        dbQuery {
            TaskReminderJobTable.deleteWhere {
                id eq jobId.toEntityId(TaskReminderJobTable)
            }
        }
    }

    override suspend fun deleteAllOfTask(taskId: IxId<TaskDto>) {
        dbQuery {
            TaskReminderJobTable.deleteWhere {
                task eq taskId.toEntityId(TaskTable)
            }
        }
    }
}