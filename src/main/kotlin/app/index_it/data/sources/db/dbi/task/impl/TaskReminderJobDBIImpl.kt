package app.index_it.data.sources.db.dbi.task.impl

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.tasks.TaskReminderJobDto
import app.index_it.data.sources.db.dbi.task.TaskReminderJobDBI
import app.index_it.data.sources.db.schemas.tasks.TaskReminderJobEntity
import app.index_it.data.sources.db.schemas.tasks.TaskReminderJobTable
import app.index_it.data.sources.db.schemas.tasks.TaskTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

object TaskReminderJobDBIImpl : TaskReminderJobDBI {
    private fun TaskReminderJobEntity.fromDto(taskReminderJobDto: TaskReminderJobDto) {
        task = taskReminderJobDto.taskId.toEntityId(TaskTable)
    }

    private fun TaskReminderJobEntity.toDto() = TaskReminderJobDto(
        id = id.toIxId(),
        taskId = task.toIxId()
    )

    override suspend fun create(taskReminderJob: TaskReminderJobDto) {
        dbQuery {
            TaskReminderJobEntity.new(taskReminderJob.id.id) {
                fromDto(taskReminderJob)
            }
        }
    }

    override suspend fun deleteAllOfTask(taskId: IxId<TaskDto>) {
        dbQuery {
            TaskReminderJobTable.deleteWhere {
                TaskReminderJobTable.task eq taskId.toEntityId(TaskTable)
            }
        }
    }
}