package app.index_it.api.routing.task.routes

import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.task.TasksRoute
import app.index_it.core.logic.usecases.TaskUseCase
import app.index_it.data.daos.list.ItemDao
import app.index_it.data.daos.task.TaskDao
import app.index_it.data.models.tasks.TaskDto
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.tasksRoute() {
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
                body<List<TaskDto>>()
            }
        }
    }) {
        val tasks = when (it.completed) {
            true ->  TaskDao.getAllCompleted(userIdFromSession()!!)
            false -> TaskDao.getAllUncompleted(userIdFromSession()!!)
            null -> TaskDao.getAll(userIdFromSession()!!)
        }

        call.respond(tasks)
    }

    post<TasksRoute>({
        tags = listOf("tasks")
        operationId = "create-task"
        summary = "creates a new task"
        request {
            body<TaskDto.TaskCreateRequestDto> {
                description = "task data"
                required = true
                example("sample-task", TaskDto.TaskCreateRequestDto("find skis", "find some skis for this winter", null, null, null, emptyList()))
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the task"
                body<TaskDto>()
            }
            HttpStatusCode.BadRequest to {
                description = "invalid parameters, see error message"
            }
            HttpStatusCode.NotFound to {
                description = "did not find the item provided for connection with this new task"
            }
        }
    }) {
        val userId = userIdFromSession()!!
        val newTask = call.receive<TaskDto.TaskCreateRequestDto>()
        val itemIdToConnect = newTask.itemId


        //////////////////
        /// VALIDATION ///
        //////////////////
        // TODO: Move to validate library
        if (itemIdToConnect != null && newTask.rrule != null) {
            return@post call.respond(HttpStatusCode.BadRequest, "cannot create recurring connected task")
        }

        if (newTask.onDayReminder != null && newTask.dueDate == null) {
            return@post call.respond(HttpStatusCode.BadRequest, "cannot add on day reminder for task without due date")
        }


        ///////////////////////
        /// ON DAY REMINDER ///
        ///////////////////////

        val onDayReminderTimestamp = TaskUseCase.calculateOnDayReminderTimestamp(newTask.dueDate, newTask.onDayReminder)

        if (onDayReminderTimestamp != null) {

        }


        /////////////////////
        /// TASK CREATION ///
        /////////////////////

        val task = if (itemIdToConnect != null) {
            ////////////////////
            /// CONNECT ITEM ///
            ////////////////////
            val itemToConnect = ItemDao.get(userId, itemIdToConnect)
                ?: return@post call.respond(HttpStatusCode.NotFound)

            val task = TaskDao.create(userIdFromSession()!!, newTask)

            ItemDao.setTaskConnection(userId, itemToConnect.listId, itemToConnect.id, task.id)
                ?: return@post call.respond(HttpStatusCode.NotFound)

            task
        } else {
            TaskDao.create(userIdFromSession()!!, newTask)
        }

        call.respond(task)

        // emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_CREATED, item)
    }

    /*
    post<TasksRoute.CreateConnectedFromItem>({
        tags = listOf("tasks")
        operationId = "create-connected-task"
        summary = "creates a new task from an existing item (aka connected task)"
        request {
            queryParameter<String>("listId") {
                description = "id of the list"
                required = true
            }
            queryParameter<String>("itemId") {
                description = "id of the item from which the task will get created"
                required = true
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the connected task"
                body<TaskDto>()
            }
            HttpStatusCode.NotFound to {
                description = "item not found"
            }
        }
    }) {
        val userId = userIdFromSession()!!

        val item = ItemDao.get(userId, it.itemId)
            ?: return@post call.respond(HttpStatusCode.NotFound)

        val task = TaskDao.createConnected(userIdFromSession()!!, item)

        ItemDao.setTaskConnection(userId, item.listId, item.id, task.id)

        call.respond(task)

        // emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_CREATED, item)
    }
     */
}