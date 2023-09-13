package app.index_it.api.routing.task.routes

import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.task.TasksRoute
import app.index_it.daos.task.TaskDao
import app.index_it.models.tasks.TaskDto
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.get
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.tasksRoute() {
    get<TasksRoute> {
        val items = when (it.completed) {
            true ->  TaskDao.getAllCompleted(userIdFromSession()!!)
            false -> TaskDao.getAllUncompleted(userIdFromSession()!!)
            null -> TaskDao.getAll(userIdFromSession()!!)
        }

        call.respond(items)
    }

    post<TasksRoute> {
        val newTask = call.receive<TaskDto.TaskCreateRequestDto>()

        val task = TaskDao.create(userIdFromSession()!!, newTask)

        call.respond(task)

        // emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_CREATED, item)
    }
}