package app.index_it.models.web

import kotlinx.serialization.Serializable

@Serializable
data class NotifyDto(
    val email: String
)
