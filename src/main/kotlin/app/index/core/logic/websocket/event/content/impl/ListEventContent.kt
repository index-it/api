package app.index.core.logic.websocket.event.content.impl

import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.websocket.event.content.WebsocketEventContent
import app.index.data.models.lists.ListData
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ListCreateOrUpdateEventContent(
    val list: ListData
) : WebsocketEventContent()

@Serializable
data class ListDeleteEventContent(
    @Contextual val listId: IxId<ListData>
) : WebsocketEventContent()