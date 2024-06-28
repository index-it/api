package app.index.data.models.lists

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * @param token should be randomly generated and hashed
 * @param email is the user that the invitation is for
 */
@Serializable
data class ListInvitationData(
    val token: String,
    val email: String,
    @Contextual val listId: IxId<ListData>,
    val editor: Boolean,
    @Contextual val expireAt: Long,
    @Contextual val createdAt: Long = DatetimeUtils.currentMillis(),
)
