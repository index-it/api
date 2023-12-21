package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

@Configuration("job")
object JobConfig {
    @ConfigurationProperty("task.reminder.job.webhook.url")
    var taskReminderJobWebhookUrl: String = "http://localhost:8080/webhook/task-reminder-job"

    @ConfigurationProperty("daily-job.webhook.url")
    var dailyJobWebhookUrl: String = "http://localhost:8080/webhook/daily-job"

    @ConfigurationProperty("daily.job.id")
    var dailyJobId: String = "daily-job"
}
