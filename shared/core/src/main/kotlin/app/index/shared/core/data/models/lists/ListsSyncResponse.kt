package app.index.shared.core.data.models.lists

import kotlinx.serialization.Serializable

@Serializable
data class ListsSyncResponse(
    val lists: List<ListData>,
    val categories: List<CategoryData>,
    val items: List<ItemData>
)
