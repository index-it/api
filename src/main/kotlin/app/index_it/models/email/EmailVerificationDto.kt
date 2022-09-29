package app.index_it.models.email

import java.util.Date
import java.util.UUID

data class EmailVerificationDto(
    val code: String = UUID.randomUUID().toString(),
    val user_email: String,
    val expire_at: Date,
    val creation_date: Date = Date()
)
