package com.index.models

import kotlinx.serialization.Serializable

/**
 * A group can contain other groups or todos.
 */
@Serializable
data class GroupDto(
    // Here `name` is used as the id
    val name: String,
)
