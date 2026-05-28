package app.index.shared.core.config

import app.index.shared.core.config.core.Configuration
import app.index.shared.core.config.core.ConfigurationProperty

@Configuration("job")
object JobConfig {
    @ConfigurationProperty("task.reminder.job.webhook.url")
    var taskReminderJobWebhookUrl: String = "https://api-dev.index-it.app/webhook/task-reminder-job"

    @ConfigurationProperty("task.reminder.job.queue.name")
    var taskReminderJobQueueName: String = "task-reminder-queue"

    @ConfigurationProperty("daily.job.webhook.url")
    var dailyJobWebhookUrl: String = "https://api-dev.index-it.app/webhook/daily-job"

    @ConfigurationProperty("daily.job.id")
    var dailyJobId: String = "dev-daily-job"
}
