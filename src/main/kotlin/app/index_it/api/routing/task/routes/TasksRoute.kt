package app.index_it.api.routing.task.routes

import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.task.TasksRoute
import app.index_it.core.exceptions.AuthenticationException
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
        val items = when (it.completed) {
            true ->  TaskDao.getAllCompleted(userIdFromSession()!!)
            false -> TaskDao.getAllUncompleted(userIdFromSession()!!)
            null -> TaskDao.getAll(userIdFromSession()!!)
        }

        call.respond(items)
    }

    post<TasksRoute>({
        tags = listOf("tasks")
        operationId = "create-task"
        summary = "creates a new task"
        request {
            body<TaskDto.TaskCreateRequestDto> {
                description = "task data"
                required = true
                example("sample-task", TaskDto.TaskCreateRequestDto("find skis", "find some skis for this winter", null, mutableListOf()))
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the task"
                body<TaskDto>()
            }
        }
    }) {
        val newTask = call.receive<TaskDto.TaskCreateRequestDto>()

        val task = TaskDao.create(userIdFromSession()!!, newTask)

        call.respond(task)

        // emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_CREATED, item)
    }

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
        println("DUDUDUHHUSADH")
        val userId = userIdFromSession() ?: throw AuthenticationException()

        val item = ItemDao.get(userId, it.itemId)
            ?: return@post call.respond(HttpStatusCode.NotFound)

        println(item)

        val task = TaskDao.createLinked(userIdFromSession()!!, item)

        ItemDao.setTaskConnection(userId, item.listId, item.id, task.id)

        call.respond(task)

        // emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_CREATED, item)
    }
}