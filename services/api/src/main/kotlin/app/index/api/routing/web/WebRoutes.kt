package app.index.api.routing.web

import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.tasks.TaskReminderJobData
import app.index.api.plugins.AuthenticationMethods
import app.index.api.routing.web.routes.revenueCatWebhookRoute
import app.index.api.routing.web.routes.webhookRoute
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.serialization.Contextual


@Resource("/webhook")
class WebhookRoute {
    @Resource("/task-reminder-job/{id}")
    class TaskReminderJobRoute(
        val parent: WebhookRoute,
        @Contextual val id: IxId<TaskReminderJobData>,
    )

    @Resource("/daily-job")
    class DailyJobRoute(val parent: WebhookRoute)

    @Resource("/revenuecat")
    class RevenueCatWebhookRoute(val parent: WebhookRoute)
}

fun Route.webRoutes() {
    authenticate(AuthenticationMethods.GOOGLE_CLOUD_OIDC) {
        webhookRoute()
    }
    authenticate(AuthenticationMethods.REVENUECAT_WEBHOOKS) {
        revenueCatWebhookRoute()
    }
}
