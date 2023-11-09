package app.index_it.api.routing.task.routes

import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.task.TasksRoute
import app.index_it.core.extentions.toObjectId
import app.index_it.data.daos.task.TaskDao
import app.index_it.data.models.tasks.SubTaskDto
import app.index_it.data.models.tasks.TaskDto
import io.github.smiley4.ktorswaggerui.dsl.resources.delete
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskRoute() {
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
                body<TaskDto>()
            }
            HttpStatusCode.NotFound to {
                description = "task not found"
            }
        }
    }) {
        val task = TaskDao.get(userIdFromSession()!!,it.taskId.toObjectId())
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
            body<TaskDto.TaskUpdateRequestDto> {
                description = "new task data"
                required = true
                example(
                    name = "sample-task-update",
                    value = TaskDto.TaskUpdateRequestDto(
                        name = "ski equipment",
                        description = "find ski equipment for winter",
                        dueDate = 1703500710000,
                        subTasks = mutableListOf(
                            SubTaskDto(
                                "skis",
                                true
                            ),
                            SubTaskDto(
                                "helmet",
                                false
                            ),
                            SubTaskDto(
                                "mask",
                                false
                            )
                        ),
                    ),
                )
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "the new task"
                body<TaskDto>()
            }
            HttpStatusCode.NotFound to {
                description = "task not found"
            }
        }
    }) {
        val updatedTask = call.receive<TaskDto.TaskUpdateRequestDto>()

        val task = TaskDao.update(userIdFromSession()!!, it.taskId.toObjectId(), updatedTask)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        call.respond(task)

        // emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_UPDATED, item)
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
        }
        response {
            HttpStatusCode.OK to {
                description = "task deleted"
            }
        }
    }) {
        // TODO: Delete related item too?
        TaskDao.delete(userIdFromSession()!!, it.taskId.toObjectId())
        call.respond(HttpStatusCode.OK)

        // emitRabbitMqWebsocketEvent(RabbitMqWebsocketEventType.ITEM_DELETED, "${it.parent.parent.listId}:${it.itemId}")
    }
}