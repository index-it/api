package app.index_it.models.auth

import app.index_it.models.user.UserDto
import io.ktor.server.auth.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
data class UserAuthSessionDto(
    @Contextual val id: Id<UserAuthSessionDto>,
    @Contextual val userId: Id<UserDto>,
    val iat: Long,
    val deviceName: String?,
    val ip: String
) : Principal
