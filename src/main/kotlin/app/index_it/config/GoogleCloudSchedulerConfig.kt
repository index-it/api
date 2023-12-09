package app.index_it.config

import app.index_it.config.core.Configuration
import app.index_it.config.core.ConfigurationProperty

@Configuration("google.cloud.scheduler")
object GoogleCloudSchedulerConfig {
    @ConfigurationProperty("project")
    lateinit var project: String
    @ConfigurationProperty("location")
    lateinit var location: String
}