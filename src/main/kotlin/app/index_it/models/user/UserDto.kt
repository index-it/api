package app.index_it.models.user

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Serializable
data class UserDto(
    @Contextual val _id: Id<UserDto> = newId(),
    val email: String,
    val username: String,
    val password_hash: String
)
