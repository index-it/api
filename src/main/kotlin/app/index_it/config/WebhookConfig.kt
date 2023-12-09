package app.index_it.config

import app.index_it.config.core.Configuration
import app.index_it.config.core.ConfigurationProperty

@Configuration("webhook")
object WebhookConfig {
    @ConfigurationProperty("task.reminder.job")
    lateinit var taskReminderJob: String
}