package app.index.core.logic.websocket.event

enum class WebsocketEventType {
    /**
     * All user auth sessions should be closed
     */
    USER_AUTH_SESSIONS_INVALIDATED,
    USER_UPDATED,

    LIST_CREATED,
    LIST_UPDATED,
    LIST_DELETED,

    CATEGORY_CREATED,
    CATEGORY_UPDATED,
    CATEGORY_DELETED,

    ITEM_CREATED,
    ITEM_UPDATED,
    ITEMS_UPDATED,
    ITEM_DELETED,
    ITEMS_DELETED,

    // NOTE: Not enabled as it would consume much network and computation power to send those events
    // ITEM_CONTENT_UPDATED,

    TASK_CREATED,
    TASK_UPDATED,
    TASKS_UPDATED,
    TASK_DELETED
}

/*
val POLYMORPHIC_WEBSOCKET_EVENT_CONTENT_CLASSES: Map<WebsocketEventType, KClass<out WebsocketEventContent>> = mapOf(
    WebsocketEventType.USER_AUTH_SESSIONS_INVALIDATED to EmptyEventContent::class,

    WebsocketEventType.LIST_CREATED to ListCreateOrUpdateEventContent::class,
    WebsocketEventType.LIST_UPDATED to ListCreateOrUpdateEventContent::class,
    WebsocketEventType.LIST_DELETED to ListDeleteEventContent::class,

    WebsocketEventType.CATEGORY_CREATED to CategoryCreateOrUpdateEventContent::class,
    WebsocketEventType.CATEGORY_UPDATED to CategoryCreateOrUpdateEventContent::class,
    WebsocketEventType.CATEGORY_DELETED to CategoryDeleteEventContent::class,

    WebsocketEventType.ITEM_CREATED to ItemCreateOrUpdateEventContent::class,
    WebsocketEventType.ITEM_UPDATED to ItemCreateOrUpdateEventContent::class,
    WebsocketEventType.ITEM_DELETED to ItemDeleteEventContent::class,

    WebsocketEventType.TASK_CREATED to TaskCreateOrUpdateEventContent::class,
    WebsocketEventType.TASK_UPDATED to TaskCreateOrUpdateEventContent::class,
    WebsocketEventType.TASK_DELETED to TaskDeleteEventContent::class
)
 */