package app.index_it.data.models.auth

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.UserDto
import io.ktor.server.auth.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Content of the auth-user-session cookie
 */
@Serializable
data class UserSessionCookie(
    @Contextual val sessionId: IxId<UserAuthSessionDto>,
    @Contextual val userId: IxId<UserDto>
) : Principal
