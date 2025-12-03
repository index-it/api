package app.index.data.models.auth

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserData
import io.ktor.server.auth.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Content of the auth-user-session cookie
 */
@Serializable
data class UserSessionCookie(
    @Contextual val session_id: IxId<UserAuthSessionData>,
    @Contextual val user_id: IxId<UserData>,
)
