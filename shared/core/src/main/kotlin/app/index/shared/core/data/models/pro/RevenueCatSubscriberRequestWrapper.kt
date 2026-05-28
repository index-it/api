package app.index.shared.core.data.models.pro

import kotlinx.serialization.Serializable

@Serializable
data class RevenueCatSubscriberRequestWrapper(
    val subscriber: RevenueCatSubscriberData
)
