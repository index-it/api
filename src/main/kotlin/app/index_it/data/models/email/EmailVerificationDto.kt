package app.index_it.data.models.email

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.schemas.user.EmailVerificationEntity
import app.index_it.data.sources.db.schemas.user.UserTable
import app.index_it.data.sources.db.toEntityId
import app.index_it.data.sources.db.toIxId
import io.ktor.util.date.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * @param token should be randomly generated and hashed
 */
@Serializable
data class EmailVerificationDto(
    val token: String,
    @Contextual val userId: IxId<UserDto>,
    @Contextual val expireAt: Long,
    @Contextual val createdAt: Long = getTimeMillis()
)

fun EmailVerificationEntity.fromDto(emailVerificationDto: EmailVerificationDto) {
    token = emailVerificationDto.token
    user = emailVerificationDto.userId.toEntityId(UserTable)
}

fun EmailVerificationEntity.toDto() = EmailVerificationDto(
    token = token,
    userId = user.toIxId(),
    expireAt = expiresAt,
    createdAt = createdAt
)