package app.index.api.data.models.web

import kotlinx.serialization.Serializable

@Serializable
data class NotifyDto(
    val email: String,
)
