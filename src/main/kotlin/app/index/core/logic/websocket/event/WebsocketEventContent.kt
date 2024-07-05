package app.index.core.logic.websocket.event

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.lists.CategoryData
import app.index.data.models.lists.ItemData
import app.index.data.models.lists.ListData
import app.index.data.models.tasks.TaskData
import app.index.data.models.user.UserData
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
    @SerialName("ITEM_DELETE")
    data class ItemDeleteEventContent(
        @Contextual val itemId: IxId<ItemData>
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
    @SerialName("TASK_DELETE")
    data class TaskDeleteEventContent(
        @Contextual val taskId: IxId<TaskData>
    ) : WebsocketEventContent()
}