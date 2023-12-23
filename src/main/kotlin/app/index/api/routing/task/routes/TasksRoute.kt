package app.index.api.routing.task.routes

import app.index.api.plugins.emitWebsocketEvent
import app.index.api.plugins.userIdFromSession
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.task.TasksRoute
import app.index.core.logic.usecases.TaskUseCase
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.core.logic.websocket.event.content.impl.ItemCreateOrUpdateEventContent
import app.index.core.logic.websocket.event.content.impl.TaskCreateOrUpdateEventContent
import app.index.data.daos.list.ItemDao
import app.index.data.daos.task.TaskDao
import app.index.data.models.tasks.TaskData
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.tasksRoute() {
    val taskDao by inject<TaskDao>()
    val itemDao by inject<ItemDao>()
    val websocketEventManager by inject<WebsocketEventManager>()

    get<TasksRoute>({
        tags = listOf("tasks")
        operationId = "get-tasks"
        summary = "gets all the tasks of a user"
        description = "gets all the tasks of a user with an optional completion filter"
        request {
            queryParameter<Boolean?>("completed") {
                required = false
                description = "completion filter: true only completed, false only uncompleted, null or missing means all"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the tasks"
                body<List<TaskData>>()
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()

        val tasks = when (it.completed) {
            true -> taskDao.getAllCompleted(userId)
            false -> taskDao.getAllUncompleted(userId)
            null -> taskDao.getAll(userId)
        }

        call.respond(tasks)
    }

    post<TasksRoute>({
        tags = listOf("tasks")
        operationId = "create-task"
        summary = "creates a new task"
        request {
            body<TaskData.TaskCreateRequestData> {
                description = "task data"
                required = true
                example(
                    "sample-task",
                    TaskData.TaskCreateRequestData("find skis", "find some skis for this winter", null, null, emptyList(), emptyList()),
                )
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the task"
                body<TaskData>()
            }
            HttpStatusCode.BadRequest to {
                description = "invalid parameters, see error message"
            }
            HttpStatusCode.NotFound to {
                description = "did not find the item provided for connection with this new task"
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()
        val newTask = call.receive<TaskData.TaskCreateRequestData>()
        val itemIdToConnect = newTask.itemId

        //////////////////
        /// VALIDATION ///
        //////////////////

        if (itemIdToConnect != null && newTask.rrule != null) {
            return@post call.respond(HttpStatusCode.BadRequest, "cannot create recurring connected task")
        }

        /////////////////////
        /// TASK CREATION ///
        /////////////////////

        val task = if (itemIdToConnect != null) {
            ////////////////////
            /// CONNECT ITEM ///
            ////////////////////
            val itemToConnect = itemDao.get(userId, itemIdToConnect)
                ?: return@post call.respond(HttpStatusCode.NotFound)

            val task = taskDao.create(userIdFromSession()!!, newTask)

            val updatedItem = itemDao.setTaskConnection(userId, itemToConnect.listId, itemToConnect.id, task.id)
                ?: return@post call.respond(HttpStatusCode.NotFound)

            emitWebsocketEvent(
                websocketEventManager = websocketEventManager,
                type = WebsocketEventType.ITEM_UPDATED,
                content = ItemCreateOrUpdateEventContent(updatedItem)
            )

            task
        } else {
            taskDao.create(userIdFromSession()!!, newTask)
        }

        /////////////////
        /// REMINDERS ///
        /////////////////
        TaskUseCase.createReminders(task)

        call.respond(task)

        emitWebsocketEvent(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.TASK_CREATED,
            content = TaskCreateOrUpdateEventContent(task)
        )
    }
}
