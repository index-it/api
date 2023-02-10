package app.index_it.models.email

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class EmailVerificationDto(
    val code: String = UUID.randomUUID().toString(),
    val user_email: String,
    @Contextual val expire_at: Date,
    @Contextual val creation_date: Date = Date()
)
