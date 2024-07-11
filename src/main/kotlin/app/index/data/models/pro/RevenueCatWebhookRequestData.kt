package app.index.data.models.pro

import kotlinx.serialization.Serializable

@Serializable
data class RevenueCatWebhookRequestData(
    val original_app_user_id: String,
    val aliases: List<String>
)
