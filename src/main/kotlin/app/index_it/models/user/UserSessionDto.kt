package app.index_it.models.user

import app.index_it.plugins.UserSessionId
import io.ktor.server.auth.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
data class UserSessionDto(
    @Contextual val id: Id<UserSessionId>,
    val iat: Long,
    @Contextual val userId: Id<UserDto>
) : Principal
