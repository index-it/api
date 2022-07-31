package app.index_it.models

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable

@Serializable
data class SessionDto(
    val id: String,
    val iat: Long,
    val userId: String
) : Principal
