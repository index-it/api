package app.index.shared.core.data.models.auth

import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.user.UserData
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
