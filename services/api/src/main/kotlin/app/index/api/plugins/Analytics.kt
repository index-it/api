package app.index.api.plugins

import app.index.api.config.BigQueryConfig
import app.index.api.core.logic.AnalyticsEventManager
import app.index.shared.core.data.models.analytics.AnalyticsEvent
import app.index.shared.core.data.models.analytics.AnalyticsEventData
import app.index.shared.core.data.models.analytics.AnalyticsEventReceiver
import io.ktor.server.routing.*

fun RoutingContext.emitAnalyticsEvent(
    analyticsEventManager: AnalyticsEventManager,
    analyticsEventData: AnalyticsEventData
) {
    val receivers = buildList {
        if (BigQueryConfig.enabled)
            add(AnalyticsEventReceiver.BIGQUERY)

        if (BigQueryConfig.console_enabled)
            add(AnalyticsEventReceiver.CONSOLE)
    }

    analyticsEventManager.emitAnalyticsEvent(AnalyticsEvent(
        data = analyticsEventData,
        receivers = receivers
    ))
}