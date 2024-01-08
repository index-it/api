package app.index.core.logic.websocket.event.content

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.ItemData
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ItemCreateOrUpdateEventContent(
    val item: ItemData
) : WebsocketEventContent()

@Serializable
data class ItemDeleteEventContent(
    @Contextual val itemId: IxId<ItemData>
) : WebsocketEventContent()