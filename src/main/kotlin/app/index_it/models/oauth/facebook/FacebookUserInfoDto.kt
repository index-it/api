package app.index_it.models.oauth.facebook

import kotlinx.serialization.Serializable

// Docs: https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api/authenticating_users_with_sign_in_with_apple
@Serializable
data class FacebookUserInfoDto(
    val email: String, // The email is verified, see https://stackoverflow.com/questions/14280535/is-it-possible-to-check-if-an-email-is-confirmed-on-facebook
)
