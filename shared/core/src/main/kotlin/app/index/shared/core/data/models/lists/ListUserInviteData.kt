package app.index.shared.core.data.models.lists

import app.index.shared.core.logic.DatetimeUtils
import app.index.shared.core.typedId.impl.IxId
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * @param token should be randomly generated and hashed
 * @param email is the user that the invitation is for
 */
@Serializable
data class ListUserInviteData(
    val token: String,
    val email: String,
    @Contextual val listId: IxId<ListData>,
    val editor: Boolean,
    @Contextual val expireAt: Long,
    @Contextual val createdAt: Long = DatetimeUtils.currentMillis(),
)
