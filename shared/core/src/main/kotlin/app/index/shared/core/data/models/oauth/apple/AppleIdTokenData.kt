package app.index.shared.core.data.models.oauth.apple

data class AppleIdTokenData(
    val email: String,
    val emailVerified: Boolean,
    val isPrivateEmail: Boolean,
)
