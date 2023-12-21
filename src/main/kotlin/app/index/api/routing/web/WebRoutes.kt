package app.index.api.routing.web

import app.index.api.routing.web.routes.notifyRoute
import app.index.api.routing.web.routes.webhookRoute
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.tasks.TaskReminderJobDto
import io.ktor.resources.*
import io.ktor.server.routing.*
import kotlinx.serialization.Contextual

@Resource("/notify/{email}")
class NotifyRoute(val email: String)

@Resource("/webhook")
class WebhookRoute {
    @Resource("/task-reminder-job/{id}")
    class TaskReminderJobRoute(
        val parent: WebhookRoute,
        @Contextual val id: IxId<TaskReminderJobDto>,
    )

    @Resource("/daily-job")
    class DailyJobRoute(val parent: WebhookRoute)
}

fun Route.webRoutes() {
    notifyRoute()
    webhookRoute()
}
