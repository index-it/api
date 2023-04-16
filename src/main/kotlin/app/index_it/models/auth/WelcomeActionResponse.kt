package app.index_it.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class WelcomeActionResponse(
    val action: WelcomeAction
)
