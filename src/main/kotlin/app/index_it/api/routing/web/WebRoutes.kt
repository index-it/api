package app.index_it.api.routing.web

import app.index_it.api.routing.web.routes.notifyRoute
import app.index_it.api.routing.web.routes.webhookRoute
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.tasks.TaskReminderJobDto
import io.ktor.resources.*
import io.ktor.server.routing.*
import kotlinx.serialization.Contextual

@Resource("/notify/{email}")
class NotifyRoute(val email: String)

@Resource("/webhook")
class WebhookRoute() {
    @Resource("/task-reminder-job/{id}")
    class TaskReminderJobRoute(val parent: WebhookRoute, @Contextual val id: IxId<TaskReminderJobDto>)
}

fun Route.webRoutes() {
    notifyRoute()
    webhookRoute()
}
