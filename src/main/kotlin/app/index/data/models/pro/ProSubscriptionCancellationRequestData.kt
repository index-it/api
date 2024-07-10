package app.index.data.models.pro

import com.stripe.param.SubscriptionUpdateParams.CancellationDetails.Feedback
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProSubscriptionCancellationRequestData(
    val comment: String? = null,
    val feedback: ProSubscriptionCancellationFeedback? = null
) {

    @Serializable
    enum class ProSubscriptionCancellationFeedback {
        @SerialName("customer_service")
        CUSTOMER_SERVICE,

        @SerialName("low_quality")
        LOW_QUALITY,

        @SerialName("missing_features")
        MISSING_FEATURES,

        @SerialName("other")
        OTHER,

        @SerialName("switched_service")
        SWITCHED_SERVICE,

        @SerialName("too_complex")
        TOO_COMPLEX,

        @SerialName("too_expensive")
        TOO_EXPENSIVE,

        @SerialName("unused")
        UNUSED;

        fun asStripeFeedback() = when (this) {
            CUSTOMER_SERVICE -> Feedback.CUSTOMER_SERVICE
            LOW_QUALITY -> Feedback.LOW_QUALITY
            MISSING_FEATURES -> Feedback.MISSING_FEATURES
            OTHER -> Feedback.OTHER
            SWITCHED_SERVICE -> Feedback.SWITCHED_SERVICE
            TOO_COMPLEX -> Feedback.TOO_COMPLEX
            TOO_EXPENSIVE -> Feedback.TOO_EXPENSIVE
            UNUSED -> Feedback.UNUSED
        }
    }
}
