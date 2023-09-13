package app.index_it.api.routing.task.routes

import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.task.TasksRoute
import app.index_it.core.extentions.toObjectId
import app.index_it.daos.list.ItemDao
import app.index_it.daos.task.TaskDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskCompletionRoute() {
    get<TasksRoute.TaskRoute.CompletionRoute> {
        val userId = userIdFromSession()!!
        val task = TaskDao.setCompletion(userId, it.parent.taskId.toObjectId(), it.completed)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        if (task.listId != null && task.itemId != null) {
            ItemDao.setCompletion(userId, task.listId, task.itemId, it.completed)
        }

        call.respond(HttpStatusCode.OK)
    }
}