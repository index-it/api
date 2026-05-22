package app.index.api.data.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class WelcomeActionResponse(
    val action: WelcomeAction,
)
