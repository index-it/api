package app.index.data.models.oauth.apple

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Docs: https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api/authenticating_users_with_sign_in_with_apple
@Serializable
data class AppleIdTokenData(
    val email: String,
    @SerialName("verified_email")
    val verifiedEmail: Boolean,
    @SerialName("is_private_email")
    val isPrivateEmail: Boolean,
)
