package app.index.data.models.pro

import kotlinx.serialization.Serializable

@Serializable
data class RevenueCatSubscriberRequestWrapper(
    val subscriber: RevenueCatSubscriberData
)
