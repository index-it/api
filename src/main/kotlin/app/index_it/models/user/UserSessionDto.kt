package app.index_it.models.user

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable

@Serializable
data class UserSessionDto(
    val id: String,
    val iat: Long,
    val userId: String
) : Principal
