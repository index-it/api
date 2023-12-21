package app.index.data.models.auth

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserDto
import io.ktor.server.auth.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class UserAuthSessionDto(
    @Contextual val id: IxId<UserAuthSessionDto>,
    @Contextual val userId: IxId<UserDto>,
    val iat: Long,
    val deviceName: String?,
    val ip: String,
) : Principal
