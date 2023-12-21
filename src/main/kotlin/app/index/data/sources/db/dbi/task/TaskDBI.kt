package app.index.data.sources.db.dbi.task

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ItemDto
import app.index.data.models.tasks.TaskDto
import app.index.data.models.user.UserDto
import app.index.data.sources.db.dbi.DBI

interface TaskDBI : DBI {
    suspend fun create(taskDto: TaskDto)

    suspend fun get(userId: IxId<UserDto>): List<TaskDto>

    suspend fun get(
        userId: IxId<UserDto>,
        taskId: IxId<TaskDto>,
    ): TaskDto?

    suspend fun exists(
        userId: IxId<UserDto>,
        taskId: IxId<TaskDto>,
    ): Boolean

    suspend fun setCompletion(
        userId: IxId<UserDto>,
        taskId: IxId<TaskDto>,
        completed: Boolean,
    ): Boolean

    suspend fun setItemConnection(
        userId: IxId<UserDto>,
        taskId: IxId<TaskDto>,
        itemId: IxId<ItemDto>?,
    ): Boolean

    suspend fun update(
        userId: IxId<UserDto>,
        taskId: IxId<TaskDto>,
        taskUpdateRequestDto: TaskDto.TaskUpdateRequestDto,
    ): Boolean

    suspend fun delete(
        userId: IxId<UserDto>,
        taskId: IxId<TaskDto>,
    )
}
