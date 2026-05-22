package app.index.api.routing.task

import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.tasks.TaskData
import app.index.api.plugins.AuthenticationMethods
import app.index.api.routing.task.routes.connectedTaskItemsRoute
import app.index.api.routing.task.routes.taskCompletionRoute
import app.index.api.routing.task.routes.taskRoute
import app.index.api.routing.task.routes.tasksRoute
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.serialization.Contextual

@Resource("tasks")
class TasksRoute(val completed: Boolean? = null) {
    @Resource("{task_id}")
    class TaskRoute(
        val parent: TasksRoute,
        @Contextual val task_id: IxId<TaskData>,
        val all: Boolean = true,
    ) {
        @Resource("completion")
        class CompletionRoute(val parent: TaskRoute, val completed: Boolean)
    }

    @Resource("connected-items")
    class ConnectedItemsRoute(val parent: TasksRoute)
}

fun Route.taskRoutes() {
    authenticate(AuthenticationMethods.USER_SESSION_AUTH) {
        tasksRoute()
        taskRoute()
        taskCompletionRoute()
        connectedTaskItemsRoute()
    }
}
