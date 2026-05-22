package app.index.api.core.logic.websocket.event

import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.lists.CategoryData
import app.index.shared.core.data.models.lists.ItemData
import app.index.shared.core.data.models.lists.ListData
import app.index.shared.core.data.models.tasks.TaskData
import app.index.shared.core.data.models.user.UserData
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class WebsocketEventContent {
    @Serializable
    @SerialName("EMPTY")
    data object EmptyEventContent : WebsocketEventContent()


    ////////////
    /// USER ///
    ////////////
    @Serializable
    @SerialName("USER_UPDATE")
    data class UserUpdateEventContent(
        val user: UserData.UserResponseDto
    ) : WebsocketEventContent()


    ////////////////
    /// CATEGORY ///
    ////////////////

    @Serializable
    @SerialName("CATEGORY_CREATE_OR_UPDATE")
    data class CategoryCreateOrUpdateEventContent(
        val category: CategoryData
    ) : WebsocketEventContent()

    @Serializable
    @SerialName("CATEGORY_DELETE")
    data class CategoryDeleteEventContent(
        @Contextual val categoryId: IxId<CategoryData>
    ) : WebsocketEventContent()


    /////////////
    /// ITEMS ///
    /////////////

    @Serializable
    @SerialName("ITEM_CREATE_OR_UPDATE")
    data class ItemCreateOrUpdateEventContent(
        val item: ItemData
    ) : WebsocketEventContent()

    @Serializable
    @SerialName("ITEMS_CREATE_OR_UPDATE")
    data class ItemsCreateOrUpdateEventContent(
        val items: List<ItemData>
    ) : WebsocketEventContent()

    @Serializable
    @SerialName("ITEM_DELETE")
    data class ItemDeleteEventContent(
        @Contextual val itemId: IxId<ItemData>
    ) : WebsocketEventContent()

    @Serializable
    @SerialName("ITEMS_DELETE")
    data class ItemsDeleteEventContent(
        val itemIds: List<@Contextual IxId<ItemData>>
    ) : WebsocketEventContent()


    ////////////
    /// LIST ///
    ////////////

    @Serializable
    @SerialName("LIST_CREATE_OR_UPDATE")
    data class ListCreateOrUpdateEventContent(
        val list: ListData
    ) : WebsocketEventContent()

    @Serializable
    @SerialName("LIST_DELETE")
    data class ListDeleteEventContent(
        @Contextual val listId: IxId<ListData>
    ) : WebsocketEventContent()


    ////////////
    /// TASK ///
    ////////////

    @Serializable
    @SerialName("TASK_CREATE_OR_UPDATE")
    data class TaskCreateOrUpdateEventContent(
        val task: TaskData
    ) : WebsocketEventContent()

    @Serializable
    @SerialName("TASKS_CREATE_OR_UPDATE")
    data class TasksUpdatedEventContent(
        val tasks: List<TaskData>
    ) : WebsocketEventContent()

    @Serializable
    @SerialName("TASK_DELETE")
    data class TaskDeleteEventContent(
        @Contextual val taskId: IxId<TaskData>
    ) : WebsocketEventContent()
}