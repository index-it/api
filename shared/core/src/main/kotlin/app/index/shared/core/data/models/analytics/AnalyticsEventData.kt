package app.index.shared.core.data.models.analytics

import app.index.shared.core.logic.DatetimeUtils
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
sealed class AnalyticsEventData {

    @Serializable
    @SerialName("USER_REGISTRATION")
    data class UserRegistrationEventData(
        val creation_source: UserData.CreationSource,
        val timestamp: String = DatetimeUtils.currentLocalDateTimeString()
    ) : AnalyticsEventData()

    @Serializable
    @SerialName("USER_LOGIN")
    data class UserLoginEventData(
        @Contextual val user_id: IxId<UserData>,
        val login_source: UserData.CreationSource,
        val timestamp: String = DatetimeUtils.currentLocalDateTimeString()
    ) : AnalyticsEventData()

    @Serializable
    @SerialName("LIST_CREATION")
    data class ListCreationEventData(
        @Contextual val user_id: IxId<UserData>,
        val timestamp: String = DatetimeUtils.currentLocalDateTimeString()
    ) : AnalyticsEventData()

    @Serializable
    @SerialName("CATEGORY_CREATION")
    data class CategoryCreationEventData(
        @Contextual val user_id: IxId<UserData>,
        @Contextual val list_id: IxId<ListData>,
        val timestamp: String = DatetimeUtils.currentLocalDateTimeString()
    ) : AnalyticsEventData()

    @Serializable
    @SerialName("ITEM_CREATION")
    data class ItemCreationEventData(
        @Contextual val user_id: IxId<UserData>,
        @Contextual val list_id: IxId<ListData>,
        @Contextual val category_id: IxId<CategoryData>?,
        val timestamp: String = DatetimeUtils.currentLocalDateTimeString()
    ) : AnalyticsEventData()

    @Serializable
    @SerialName("ITEM_COMPLETION")
    data class ItemCompletionEventData(
        @Contextual val user_id: IxId<UserData>,
        @Contextual val list_id: IxId<ListData>,
        @Contextual val category_id: IxId<CategoryData>?,
        val completed: Boolean,
        val timestamp: String = DatetimeUtils.currentLocalDateTimeString()
    ) : AnalyticsEventData()

    @Serializable
    @SerialName("TASK_CREATION")
    data class TaskCreationEventData(
        @Contextual val user_id: IxId<UserData>,
        @Contextual val item_id: IxId<ItemData>?,
        val sub_tasks_count: Int,
        val reminders_count: Int,
        val is_recurring: Boolean,
        val priority: Int?,
        val timestamp: String = DatetimeUtils.currentLocalDateTimeString()
    ) : AnalyticsEventData()

    @Serializable
    @SerialName("TASK_COMPLETION")
    data class TaskCompletionEventData(
        @Contextual val user_id: IxId<UserData>,
        @Contextual val task_id: IxId<TaskData>,
        val completed: Boolean,
        val timestamp: String = DatetimeUtils.currentLocalDateTimeString()
    ) : AnalyticsEventData()
}