package app.index.data.models.tasks

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * @property id job id
 * @property task
 */
@Serializable
data class TaskReminderJobDto(
    @Contextual val id: IxId<TaskReminderJobDto>,
    @Contextual val task: TaskDto,
    @Contextual val userId: IxId<UserDto>,
)
