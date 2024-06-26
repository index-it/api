package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

/**
 * Configuration for the Big Query client for analytics
 */
@Configuration("bigquery")
object BigQueryConfig {
    /**
     * If disabled analytics events will be pushed to the console only
     */
    @ConfigurationProperty("enabled")
    var enabled: Boolean = false

    /**
     * If enabled all events will also be emitted to the console
     */
    @ConfigurationProperty("console.enabled")
    var console_enabled: Boolean = true

    /**
     * The name of the dataset.
     *
     * This must exist aka be created via the GCP console
     */
    @ConfigurationProperty("dataset.name")
    var datasetName: String = "index_dev"

    /**
     * Amount of threads used by the scheduler
     */
    @ConfigurationProperty("thread.pool.size")
    var poolSize: Int = 8

    /**
     * Time between one push and another (in seconds)
     */
    @ConfigurationProperty("push.interval")
    var interval: Long = 30

    /**
     * Initial push delay (in seconds)
     */
    @ConfigurationProperty("push.delay")
    var delay: Long = 10

    /**
     * Maximum amount of events that can be kept in memory without pushing
     * When reached a push will be performed even if the interval hasn't ended
     */
    @ConfigurationProperty("max.events.entry")
    var maxEventsEntry: Long = 1_000
}