package app.index.api.routing.task

import app.index.api.plugins.AuthenticationMethods
import app.index.api.routing.task.routes.taskCompletionRoute
import app.index.api.routing.task.routes.taskRoute
import app.index.api.routing.task.routes.tasksRoute
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.tasks.TaskDto
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.serialization.Contextual

@Resource("tasks")
class TasksRoute(val completed: Boolean? = null) {
    // TODO: Decide whether to remove or not
    // @Resource("connection")
    // class CreateConnectedFromItem(val parent: TasksRoute, @Contextual val itemId: IxId<ItemDto>) {}

    @Resource("{taskId}")
    class TaskRoute(
        val parent: TasksRoute,
        @Contextual val taskId: IxId<TaskDto>,
        val all: Boolean = true,
    ) {
        // TODO: Decide whether to remove or not
        // @Resource("connection")
        // class ConnectionRoute(val parent: TaskRoute, @Contextual val itemId: IxId<ItemDto>? = null) {}

        @Resource("completion")
        class CompletionRoute(val parent: TaskRoute, val completed: Boolean)
    }
}

fun Route.taskRoutes() {
    authenticate(AuthenticationMethods.USER_SESSION_AUTH) {
        tasksRoute()
        taskRoute()
        // taskConnectionRoute()
        taskCompletionRoute()
    }
}
