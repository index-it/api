package app.index.core.logic.websocket.event.content.impl

import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.websocket.event.content.WebsocketEventContent
import app.index.data.models.tasks.TaskData
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class TaskCreateOrUpdateEventContent(
    val task: TaskData
) : WebsocketEventContent()

@Serializable
data class TaskDeleteEventContent(
    @Contextual val taskId: IxId<TaskData>
) : WebsocketEventContent()