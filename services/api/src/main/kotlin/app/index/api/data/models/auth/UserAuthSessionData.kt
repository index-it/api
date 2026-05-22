package app.index.api.data.models.auth

import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.user.UserData
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
