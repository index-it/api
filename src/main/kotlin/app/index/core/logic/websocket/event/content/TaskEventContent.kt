package app.index.core.logic.websocket.event.content

import app.index.core.logic.typedId.impl.IxId
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