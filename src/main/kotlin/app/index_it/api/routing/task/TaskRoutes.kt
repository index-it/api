package app.index_it.api.routing.task

import app.index_it.api.plugins.AuthenticationMethods
import app.index_it.api.routing.task.routes.taskCompletionRoute
import app.index_it.api.routing.task.routes.taskLinkingRoute
import app.index_it.api.routing.task.routes.taskRoute
import app.index_it.api.routing.task.routes.tasksRoute
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.tasks.TaskDto
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.serialization.Contextual

@Resource("tasks")
class TasksRoute(val completed: Boolean? = null) {
    @Resource("linked")
    class LinkedRoute(val parent: TasksRoute, @Contextual val itemId: IxId<ItemDto>) {}

    @Resource("{taskId}")
    class TaskRoute(val parent: TasksRoute, @Contextual val taskId: IxId<TaskDto>) {
        @Resource("completion")
        class CompletionRoute(val parent: TaskRoute, val completed: Boolean) {}

        @Resource("linking")
        class LinkingRoute(val parent: TaskRoute, @Contextual val itemId: IxId<ItemDto>?) {}
    }
}

fun Route.taskRoutes() {
    authenticate(AuthenticationMethods.USER_SESSION_AUTH) {
        tasksRoute()
        taskRoute()
        taskLinkingRoute()
        taskCompletionRoute()
    }
}
