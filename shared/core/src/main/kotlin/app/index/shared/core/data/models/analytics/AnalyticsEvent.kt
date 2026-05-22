package app.index.shared.core.data.models.analytics

import kotlinx.serialization.Serializable

/**
 * @param data
 * @param receivers
 */
@Serializable
data class AnalyticsEvent(
    val data: AnalyticsEventData,
    val receivers: List<AnalyticsEventReceiver>
) {
    @Serializable
    data class BigQueryConsumableAnalyticsEvent(
        val data: AnalyticsEventData,
    )

    fun asBigQueryConsumable() = BigQueryConsumableAnalyticsEvent(data)
}