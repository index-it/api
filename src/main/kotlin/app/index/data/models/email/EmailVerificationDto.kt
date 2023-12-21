package app.index.data.models.email

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * @param token should be randomly generated and hashed
 */
@Serializable
data class EmailVerificationDto(
    val token: String,
    @Contextual val userId: IxId<UserDto>,
    @Contextual val expireAt: Long,
    @Contextual val createdAt: Long = DatetimeUtils.currentMillis(),
)
