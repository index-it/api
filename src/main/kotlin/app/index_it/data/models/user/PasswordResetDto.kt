package app.index_it.data.models.user

import app.index_it.core.logic.currentMillis
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.sources.db.schemas.user.PasswordResetEntity
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
data class PasswordResetDto(
    val token: String,
    @Contextual val userId: IxId<UserDto>,
    @Contextual val expireAt: Long,
    @Contextual val creationDate: Long = currentMillis() // TODO: Rename
)

fun PasswordResetEntity.fromDto(passwordResetDto: PasswordResetDto) {
    token = passwordResetDto.token
    user = passwordResetDto.userId.toEntityId(UserTable)
    expiresAt = passwordResetDto.expireAt
    createdAt = passwordResetDto.creationDate
}

fun PasswordResetEntity.toDto() = PasswordResetDto(
    token = token,
    userId = user.toIxId(),
    expireAt = expiresAt,
    creationDate = createdAt
)