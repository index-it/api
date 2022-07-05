package com.index.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Serializable
data class UserDto(
    @Contextual val _id: Id<UserDto> = newId(),
    val username: String,
)
