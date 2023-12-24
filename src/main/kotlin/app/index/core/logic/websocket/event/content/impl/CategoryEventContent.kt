package app.index.core.logic.websocket.event.content.impl

import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.websocket.event.content.WebsocketEventContent
import app.index.data.models.lists.CategoryData
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class CategoryCreateOrUpdateEventContent(
    val category: CategoryData
) : WebsocketEventContent()

@Serializable
data class CategoryDeleteEventContent(
    @Contextual val categoryId: IxId<CategoryData>
) : WebsocketEventContent()