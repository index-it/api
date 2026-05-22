package app.index.shared.core.data.models.pro

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RevenueCatWebhookRequestData(
    val original_app_user_id: String,
    val aliases: List<String>,
    val environment: RevenueCatEnvironment? = null
) {

    @Serializable
    enum class RevenueCatEnvironment {
        @SerialName("SANDBOX")
        SANDBOX,

        @SerialName("PRODUCTION")
        PRODUCTION,
    }
}
