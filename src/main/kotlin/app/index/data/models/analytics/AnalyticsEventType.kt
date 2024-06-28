package app.index.data.models.analytics

enum class AnalyticsEventType {
    USER_REGISTRATION,
    USER_LOGIN,
    LIST_CREATION,
    CATEGORY_CREATION,
    ITEM_CREATION,
    ITEM_COMPLETION,
    TASK_CREATION,
    TASK_COMPLETION;

    companion object {
        /**
         * @return the corresponding [AnalyticsEventType] depending on the [AnalyticsEventData], or null for no matches
         */
        fun getForData(eventData: AnalyticsEventData): AnalyticsEventType? {
            return when (eventData::class) {
                AnalyticsEventData.UserRegistrationEventData::class -> USER_REGISTRATION
                AnalyticsEventData.UserLoginEventData::class -> USER_LOGIN
                AnalyticsEventData.ListCreationEventData::class -> LIST_CREATION
                AnalyticsEventData.CategoryCreationEventData::class -> CATEGORY_CREATION
                AnalyticsEventData.ItemCreationEventData::class -> ITEM_CREATION
                AnalyticsEventData.ItemCompletionEventData::class -> ITEM_COMPLETION
                AnalyticsEventData.TaskCreationEventData::class -> TASK_CREATION
                AnalyticsEventData.TaskCompletionEventData::class -> TASK_COMPLETION
                else -> null
            }
        }
    }
}