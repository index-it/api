package app.index.data.models.oauth.google

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleUserInfoData(
    val email: String,
    @SerialName("verified_email")
    val verifiedEmail: Boolean,
)
