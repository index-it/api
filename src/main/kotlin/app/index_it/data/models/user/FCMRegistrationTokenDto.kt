package app.index_it.data.models.user

import app.index_it.core.logic.DatetimeUtils
import app.index_it.core.logic.typedId.impl.IxId
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class FCMRegistrationTokenDto(
    val token: String,
    @Contextual val userId: IxId<UserDto>,
    @Contextual val createdAt: Long = DatetimeUtils.currentMillis()
)