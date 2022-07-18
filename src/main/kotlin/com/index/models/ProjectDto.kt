package com.index.models

import com.index.models.user.UserDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

/**
 * A project is like a folder / category of todos.
 */
@Serializable
data class ProjectDto(
    @Contextual val _id: Id<ProjectDto> = newId(),
    @Contextual val owner_id: Id<UserDto>,
    val name: String,
    // TODO: Add default statuses
    val statuses: MutableList<StatusDto>,
    val groups: MutableList<GroupDto>
)
