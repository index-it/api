package app.index.shared.core.data.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class WelcomeActionResponse(
    val action: WelcomeAction,
)
