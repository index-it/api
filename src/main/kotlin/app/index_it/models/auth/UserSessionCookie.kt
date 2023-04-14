package app.index_it.models.auth

import app.index_it.models.user.UserDto
import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

/**
 * Content of the auth-user-session cookie
 */
@Serializable
data class UserSessionCookie(
    val sessionId: Id<UserAuthSessionDto>,
    val userId: Id<UserDto>
) : Principal
