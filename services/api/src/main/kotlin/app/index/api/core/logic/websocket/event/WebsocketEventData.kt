package app.index.api.core.logic.websocket.event

import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.auth.UserAuthSessionData
import app.index.shared.core.data.models.user.UserData
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Represents a websocket event that should be sent to a user
 *
 * @param fromSessionId the user auth session that triggered this event
 * @param fromUserId the id of the user that triggered this event
 * @param targetUsers the users that should receive this event
 * @param type
 * @param inclusive indicates whether the event is emitted for the auth session that generated it too
 * @param content polymorphic data depending on the [type]
 */
@Serializable
data class WebsocketEventData(
    @Contextual val fromSessionId: IxId<UserAuthSessionData>?,
    @Contextual val fromUserId: IxId<UserData>?,
    val targetUsers: Set<@Contextual IxId<UserData>>,
    val type: WebsocketEventType,
    val inclusive: Boolean,
    val content: WebsocketEventContent
) {
    @Serializable
    data class WebsocketEventSanitizedData(
        @Contextual val fromSessionId: IxId<UserAuthSessionData>?,
        @Contextual val fromUserId: IxId<UserData>?,
        val type: WebsocketEventType,
        val inclusive: Boolean,
        val content: WebsocketEventContent
    )

    fun sanitize() = WebsocketEventSanitizedData(
        fromSessionId = fromSessionId,
        fromUserId = fromUserId,
        type = type,
        inclusive = inclusive,
        content = content
    )
}