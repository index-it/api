package app.index.core.logic.websocket.event

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.auth.UserAuthSessionData
import app.index.data.models.user.UserData
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Represents a websocket event that should be sent to a user
 *
 * @param fromSessionId the user auth session that triggered this event
 * @param fromUserId the id of the user that triggered this event
 * @param type
 * @param content Serialized [WebsocketEventContent]
 */
@Serializable
data class WebsocketEventData(
    @Contextual val fromSessionId: IxId<UserAuthSessionData>,
    @Contextual val fromUserId: IxId<UserData>,
    val type: WebsocketEventType,
    val content: String
)