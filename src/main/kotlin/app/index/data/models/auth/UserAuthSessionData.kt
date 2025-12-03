package app.index.data.models.auth

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserData
import io.ktor.server.auth.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class UserAuthSessionData(
    @Contextual val id: IxId<UserAuthSessionData>,
    @Contextual val userId: IxId<UserData>,
    val iat: Long,
    val deviceName: String?,
    val ip: String,
)
