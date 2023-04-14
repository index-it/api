package app.index_it.models.user

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import java.util.*


/**
 * @param token should be randomly generated and hashed
 */
@Serializable
data class PasswordResetDto(
    val token: String,
    @Contextual val userId: Id<UserDto>,
    @Contextual val expireAt: Date,
    @Contextual val creationDate: Date = Date()
)
