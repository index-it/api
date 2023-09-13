package app.index_it.api.routing.task.routes

import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.task.TasksRoute
import app.index_it.core.extentions.toObjectId
import app.index_it.daos.task.TaskDao
import app.index_it.models.tasks.TaskDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskRoute() {
    get<TasksRoute.TaskRoute> {
        val task = TaskDao.get(userIdFromSession()!!,it.taskId.toObjectId())
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(task)
    }

    put<TasksRoute.TaskRoute> {
        val updatedTask = call.receive<TaskDto.TaskUpdateRequestDto>()

        val task = TaskDao.update(userIdFromSession()!!, it.taskId.toObjectId(), updatedTask)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(task)

        // emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_UPDATED, item)
    }

    delete<TasksRoute.TaskRoute> {
        TaskDao.delete(userIdFromSession()!!, it.taskId.toObjectId())
        call.respond(HttpStatusCode.OK)

        // emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_DELETED, "${it.parent.parent.listId}:${it.itemId}")
    }
}