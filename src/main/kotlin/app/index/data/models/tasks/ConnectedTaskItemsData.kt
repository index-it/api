package app.index.data.models.tasks

import app.index.data.models.lists.CategoryData
import app.index.data.models.lists.ItemData
import app.index.data.models.lists.ListData
import kotlinx.serialization.Serializable

@Serializable
data class ConnectedTaskItemsData(
    val items: List<ItemData>,
    val categories: List<CategoryData>,
    val lists: List<ListData>
)