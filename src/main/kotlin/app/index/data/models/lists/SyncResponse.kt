package app.index.data.models.lists

data class ListsSyncResponse(
    val lists: List<ListData>,
    val categories: List<CategoryData>,
    val items: List<ItemData>
)
