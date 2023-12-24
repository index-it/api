package app.index.api.routing.task.routes

import app.index.api.plugins.emitWebsocketEvent
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.task.TasksRoute
import app.index.core.logic.usecases.TaskUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.core.logic.websocket.event.content.impl.ItemCreateOrUpdateEventContent
import app.index.core.logic.websocket.event.content.impl.TaskCreateOrUpdateEventContent
import app.index.core.logic.websocket.event.content.impl.TaskDeleteEventContent
import app.index.data.daos.list.ItemDao
import app.index.data.daos.task.TaskDao
import app.index.data.daos.task.TaskReminderJobDao
import app.index.data.models.tasks.SubTaskData
import app.index.data.models.tasks.TaskData
import io.github.smiley4.ktorswaggerui.dsl.resources.delete
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDate
import org.koin.ktor.ext.inject

fun Route.taskRoute() {
    val taskDao by inject<TaskDao>()
    val taskReminderJobDao by inject<TaskReminderJobDao>()
    val itemDao by inject<ItemDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

    get<TasksRoute.TaskRoute>({
        tags = listOf("tasks")
        operationId = "get-task"
        summary = "gets a single task"
        request {
            pathParameter<String>("taskId") {
                required = true
                description = "the id of the task"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the task"
                body<TaskData>()
            }
            HttpStatusCode.NotFound to {
                description = "task not found"
            }
        }
    }) {
        val task = taskDao.get(userIdFromSessionOrThrow(), it.task_id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(task)
    }

    put<TasksRoute.TaskRoute>({
        tags = listOf("tasks")
        operationId = "update-task"
        summary = "updates a task"
        request {
            pathParameter<String>("taskId") {
                required = true
                description = "the id of the task"
            }
            body<TaskData.TaskUpdateRequestData> {
                description = "new task data"
                required = true
                example(
                    name = "sample-task-update",
                    value =
                    TaskData.TaskUpdateRequestData(
                        name = "ski equipment",
                        description = "find ski equipment for winter",
                        due_date = LocalDate(2023, 12, 2),
                        subtasks =
                        mutableListOf(
                            SubTaskData(
                                "skis",
                                true,
                            ),
                            SubTaskData(
                                "helmet",
                                false,
                            ),
                            SubTaskData(
                                "goggles",
                                false,
                            ),
                        ),
                    ),
                )
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the updated task"
                body<TaskData>()
            }
            HttpStatusCode.NotFound to {
                description = "task or item to connect not found"
            }
            HttpStatusCode.MethodNotAllowed to {
                description = "cannot set an rrule for a task connected to an item (cannot make task recurrent if connected to an item)"
            }
        }
    }) {
        val updateData = call.receive<TaskData.TaskUpdateRequestData>()
        val userId = userIdFromSessionOrThrow()
        val taskId = it.task_id
        val newItemIdToConnect = updateData.item_id

        val task = taskDao.get(userId, it.task_id)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        if (task.item_id != null && updateData.rrule != null) {
            return@put call.respond(HttpStatusCode.MethodNotAllowed)
        }

        if (task.item_id != newItemIdToConnect) {
            val originalConnectedItem = task.item_id?.let { originalItemId -> itemDao.get(userId, originalItemId) }

            // un-connects the old item if existing
            if (originalConnectedItem != null) {
                itemDao.setTaskConnection(userId, originalConnectedItem.list_id, originalConnectedItem.id, null)
                    ?.also { updatedOriginalConnectedItem ->
                        emitWebsocketEvent(
                            websocketEventManager = websocketEventManager,
                            type = WebsocketEventType.ITEM_UPDATED,
                            content = ItemCreateOrUpdateEventContent(updatedOriginalConnectedItem)
                        )
                    }
            }

            // connects the new item if required
            if (newItemIdToConnect != null) {
                val newConnectedItem = itemDao.get(userId, newItemIdToConnect)
                    ?: return@put call.respond(HttpStatusCode.NotFound)

                val updatedNewConnectedItem = itemDao.setTaskConnection(userId, newConnectedItem.list_id, newConnectedItem.id, taskId)
                    ?: return@put call.respond(HttpStatusCode.NotFound)

                emitWebsocketEvent(
                    websocketEventManager = websocketEventManager,
                    type = WebsocketEventType.ITEM_UPDATED,
                    content = ItemCreateOrUpdateEventContent(updatedNewConnectedItem)
                )
            }
        }

        val updatedTask = taskDao.update(userId, taskId, updateData)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        TaskUseCase.refreshReminders(updatedTask)

        call.respond(updatedTask)

        emitWebsocketEvent(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.TASK_UPDATED,
            content = TaskCreateOrUpdateEventContent(updatedTask)
        )
    }

    delete<TasksRoute.TaskRoute>({
        tags = listOf("tasks")
        operationId = "delete-task"
        summary = "deletes a task"
        request {
            pathParameter<String>("taskId") {
                required = true
                description = "the id of the task"
            }
            queryParameter<Boolean>("all") {
                required = false
                description = "only useful when a task is recurrent: true for deleting all occurrences, false to keep recurrent tasks"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "task deleted"
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()

        taskReminderJobDao.deleteAllOfTask(it.task_id)

        if (!it.all) {
            taskDao.get(userId, it.task_id)?.let { task ->
                TaskUseCase.createNextOccurrence(task)
                    ?.also { nextOccurrenceTask ->
                        emitWebsocketEvent(
                            websocketEventManager = websocketEventManager,
                            type = WebsocketEventType.TASK_CREATED,
                            content = TaskCreateOrUpdateEventContent(nextOccurrenceTask)
                        )
                    }
            }
        }

        val deleted = taskDao.delete(userId, it.task_id)

        call.respond(HttpStatusCode.OK)

        if (deleted) {
            emitWebsocketEvent(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.TASK_DELETED,
                content = TaskDeleteEventContent(it.task_id)
            )
        }
    }
}
