package app.index_it.api.routing.list.routes

import app.index_it.api.plugins.userIdFromSession
import app.index_it.api.routing.list.ListsRoute
import app.index_it.core.extentions.toObjectId
import app.index_it.daos.list.ItemDao
import app.index_it.daos.task.TaskDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.itemCompletionRoute() {
    get<ListsRoute.ListRoute.ItemsRoute.ItemRoute.CompletionRoute> {
        val userId = userIdFromSession()!!
        // TODO: Maybe change string args to Id<> ones since they support serialization?
        val item = ItemDao.setCompletion(userId, it.parent.parent.parent.listId.toObjectId(), it.parent.itemId.toObjectId(), it.completed)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        if (item.taskId != null) {
            TaskDao.setCompletion(userId, item.taskId, it.completed)
        }

        call.respond(HttpStatusCode.OK)
    }
}