package app.index_it.api.routing.task

import app.index_it.api.plugins.AuthenticationMethods
import app.index_it.api.routing.task.routes.taskCompletionRoute
import app.index_it.api.routing.task.routes.taskLinkingRoute
import app.index_it.api.routing.task.routes.taskRoute
import app.index_it.api.routing.task.routes.tasksRoute
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

@Resource("tasks")
class TasksRoute(val completed: Boolean? = null) {
    @Resource("linked")
    class LinkedRoute(val parent: TasksRoute, val listId: String, val itemId: String) {}

    @Resource("{taskId}")
    class TaskRoute(val parent: TasksRoute, val taskId: String) {
        @Resource("completion")
        class CompletionRoute(val parent: TaskRoute, val completed: Boolean) {}

        @Resource("linking")
        class LinkingRoute(val parent: TaskRoute, val listId: String?, val itemId: String?) {}
    }
}

fun Route.taskRoutes() {
    authenticate(AuthenticationMethods.userSessionAuth) {
        tasksRoute()
        taskRoute()
        taskLinkingRoute()
        taskCompletionRoute()
    }
}
