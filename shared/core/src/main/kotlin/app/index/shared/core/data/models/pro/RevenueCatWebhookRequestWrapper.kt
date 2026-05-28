package app.index.shared.core.data.models.pro

import kotlinx.serialization.Serializable

@Serializable
data class RevenueCatWebhookRequestWrapper(
    val event: RevenueCatWebhookRequestData
)
