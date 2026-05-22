package app.index.api.data.models.tasks

import app.index.api.data.models.lists.CategoryData
import app.index.api.data.models.lists.ItemData
import app.index.api.data.models.lists.ListData
import kotlinx.serialization.Serializable

@Serializable
data class ConnectedTaskItemsData(
    val items: List<ItemData>,
    val categories: List<CategoryData>,
    val lists: List<ListData>
)