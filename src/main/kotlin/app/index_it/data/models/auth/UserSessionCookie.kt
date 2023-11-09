package app.index_it.data.models.auth

import app.index_it.data.models.user.UserDto
import io.ktor.server.auth.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

/**
 * Content of the auth-user-session cookie
 */
@Serializable
data class UserSessionCookie(
    @Contextual val sessionId: Id<UserAuthSessionDto>,
    @Contextual val userId: Id<UserDto>
) : Principal
