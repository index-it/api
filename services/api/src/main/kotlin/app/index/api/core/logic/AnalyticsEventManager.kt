package app.index.api.core.logic

import app.index.api.core.clients.BigQueryClient
import app.index.api.data.models.analytics.AnalyticsEvent
import app.index.api.data.models.analytics.AnalyticsEventReceiver
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

private val log = KotlinLogging.logger {  }

/**
 * Manager for analytics events
 *
 * @see emitAnalyticsEvent
 */
@Single(createdAtStart = true)
class AnalyticsEventManager(
    private val bigQueryClient: BigQueryClient
) {

    /**
     * Emits an analytics event.
     *
     * Automatically handles correct emitting based on [AnalyticsEvent.receivers]
     *
     * @param analyticsEvent
     */
    fun emitAnalyticsEvent(analyticsEvent: AnalyticsEvent) {
        log.debug { "received analytics event $analyticsEvent" }

        for (receiver in analyticsEvent.receivers) {
            when (receiver) {
                AnalyticsEventReceiver.BIGQUERY -> emitToBigQuery(analyticsEvent)
                AnalyticsEventReceiver.CONSOLE -> emitToConsole(analyticsEvent)
            }
        }
    }

    private fun emitToBigQuery(analyticsEvent: AnalyticsEvent) {
        bigQueryClient.pushAnalyticsEvent(analyticsEvent.asBigQueryConsumable())
    }

    private fun emitToConsole(analyticsEvent: AnalyticsEvent) {
        log.info { "received analytics event: ${analyticsEvent.data}" }
    }
}