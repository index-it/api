package app.index.data.models.web

import kotlinx.serialization.Serializable

@Serializable
data class NotifyDto(
    val email: String,
)
