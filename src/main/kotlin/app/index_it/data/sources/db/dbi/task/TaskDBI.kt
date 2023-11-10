package app.index_it.data.sources.db.dbi.task

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.dbi.DBI

interface TaskDBI : DBI {
    suspend fun exists(id: IxId<TaskDto>): Boolean
    suspend fun create(taskDto: TaskDto)
    suspend fun get(id: IxId<UserDto>): List<TaskDto>
    suspend fun get(id: IxId<TaskDto>): TaskDto?
    suspend fun setCompletion(id: IxId<TaskDto>, completed: Boolean): TaskDto?
    suspend fun setLinking(taskId: IxId<TaskDto>, itemId: IxId<TaskDto>): TaskDto?
    suspend fun update(id: IxId<TaskDto>, taskUpdateRequestDto: TaskDto.TaskUpdateRequestDto): TaskDto?
    suspend fun delete(id: IxId<TaskDto>)
}