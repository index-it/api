package app.index.data.models.pro

import kotlinx.serialization.Serializable

@Serializable
data class RevenueCatSubscriberData(
    val entitlements: Map<String, RevenueCatEntitlement>
)