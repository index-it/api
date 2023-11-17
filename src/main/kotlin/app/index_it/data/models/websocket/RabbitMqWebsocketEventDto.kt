package app.index_it.data.models.websocket

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.auth.UserAuthSessionDto
import app.index_it.data.models.user.UserDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
enum class RabbitMqWebsocketEventType(val realTimeDataKind: Boolean = true) {
    CLOSE_ALL_CLIENT_CONNECTIONS(false),

    LIST_CREATED,
    LIST_UPDATED,
    LIST_DELETED,

    ITEM_CREATED,
    ITEM_UPDATED,
    ITEM_DELETED,

    CATEGORY_CREATED,
    CATEGORY_UPDATED,
    CATEGORY_DELETED,
}

@Serializable
data class RabbitMqWebsocketEventDto(
    @Contextual val fromSessionId: IxId<UserAuthSessionDto>,
    @Contextual val fromUserId: IxId<UserDto>,
    val eventType: RabbitMqWebsocketEventType,
    // Serialized event data (already JSON encoded)
    val eventData: String?,
)
