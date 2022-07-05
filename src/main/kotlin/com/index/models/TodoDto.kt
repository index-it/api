package com.index.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Serializable
data class TodoDto(
    @Contextual val _id: Id<TodoDto> = newId(),
    @Contextual val project_id: Id<ProjectDto>,
    val grouping: String,
    val status: String,
    val name: String,
)
