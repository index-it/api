package app.index_it.data.sources.db.dbi.task

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.DBI

interface TaskDBI : DBI {
    suspend fun create(taskDto: TaskDto)
    suspend fun get(userId: IxId<UserDto>): List<TaskDto>
    suspend fun get(userId: IxId<UserDto>, taskId: IxId<TaskDto>): TaskDto?
    suspend fun exists(userId: IxId<UserDto>, taskId: IxId<TaskDto>): Boolean
    suspend fun setCompletion(userId: IxId<UserDto>, taskId: IxId<TaskDto>, completed: Boolean): Boolean
    suspend fun setLinking(userId: IxId<UserDto>, taskId: IxId<TaskDto>, itemId: IxId<ItemDto>?): Boolean
    suspend fun update(userId: IxId<UserDto>, taskId: IxId<TaskDto>, taskUpdateRequestDto: TaskDto.TaskUpdateRequestDto): Boolean
    suspend fun delete(userId: IxId<UserDto>, taskId: IxId<TaskDto>)
}