package app.index.api.data.models.email

import app.index.api.core.logic.DatetimeUtils
import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.user.UserData
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * @param token should be randomly generated and hashed
 */
@Serializable
data class EmailVerificationData(
    val token: String,
    @Contextual val userId: IxId<UserData>,
    @Contextual val expireAt: Long,
    @Contextual val createdAt: Long = DatetimeUtils.currentMillis(),
)
