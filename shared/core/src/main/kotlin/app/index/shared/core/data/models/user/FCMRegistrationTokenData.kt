package app.index.shared.core.data.models.user

import app.index.shared.core.logic.DatetimeUtils
import app.index.shared.core.typedId.impl.IxId
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class FCMRegistrationTokenData(
    val token: String,
    @Contextual val userId: IxId<UserData>,
    @Contextual val createdAt: Long = DatetimeUtils.currentMillis(),
) {
    @Serializable
    data class FCMRegistrationTokenRequestBody(
        val token: String,
    )
}
