package app.index.data.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class WelcomeActionResponse(
    val action: WelcomeAction,
)
