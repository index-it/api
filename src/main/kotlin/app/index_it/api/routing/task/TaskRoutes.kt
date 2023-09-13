package app.index_it.api.routing.task

import app.index_it.api.plugins.AuthenticationMethods
import app.index_it.api.routing.task.routes.taskCompletionRoute
import app.index_it.api.routing.task.routes.taskRoute
import app.index_it.api.routing.task.routes.tasksRoute
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

@Resource("tasks")
class TasksRoute(val completed: Boolean? = null) {
    @Resource("{taskId}")
    class TaskRoute(val parent: TasksRoute, val taskId: String) {
        @Resource("completion")
        class CompletionRoute(val parent: TaskRoute, val completed: Boolean) {}
    }
}

fun Route.taskRoutes() {
    authenticate(AuthenticationMethods.userSessionAuth) {
        tasksRoute()
        taskRoute()
        taskCompletionRoute()
    }
}
