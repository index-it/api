package app.index_it.models.oauth.google

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleUserInfoDto(
    val email: String,
    @SerialName("verified_email")
    val verifiedEmail: Boolean
)
