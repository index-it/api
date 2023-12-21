package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

@Configuration("job")
object JobConfig {
    @ConfigurationProperty("task.reminder.webhook.url")
    var taskReminderWebhookUrl: String = "http://localhost:8080/webhook/task-reminder-job"

    @ConfigurationProperty("fcm.registration.token.expiration.webhook.url")
    var fcmRegistrationTokenExpirationWebhookUrl: String = "http://localhost:8080/webhook/registration-token-expiration-job"

    @ConfigurationProperty("fcm.registration.token.expiration.job.id")
    var fcmRegistrationTokenExpirationJobId: String = "registration-token-expiration"
}
