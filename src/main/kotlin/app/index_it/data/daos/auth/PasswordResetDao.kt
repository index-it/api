package app.index_it.data.daos.auth

import app.index_it.core.clients.SendinblueClient
import app.index_it.core.logic.TokenGenerator
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.PasswordResetDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.models.user.fromDto
import app.index_it.data.models.user.toDto
import app.index_it.data.sources.db.schemas.user.PasswordResetEntity
import app.index_it.data.sources.db.schemas.user.PasswordResetTable
import app.index_it.data.sources.db.schemas.user.UserTable
import app.index_it.data.sources.db.toEntityId
import io.ktor.util.date.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object PasswordResetDao {
    private fun save(passwordResetDto: PasswordResetDto) = PasswordResetEntity.new {
        fromDto(passwordResetDto)
    }

    fun get(token: String): PasswordResetDto? {
        return PasswordResetEntity
            .find { PasswordResetTable.token eq TokenGenerator.hashToken(token) }
            .limit(1)
            .firstOrNull()
            ?.toDto()
    }

    /**
     * Sends a password reset email to the provided email
     * and returns true if the email was sent successfully, false otherwise
     */
    suspend fun createAndSend(user: UserDto): Boolean {
        val (token, hashedToken) = TokenGenerator.generate()

        val passwordResetDto = PasswordResetDto(
            token = hashedToken,
            userId = user.id,
            expireAt = getTimeMillis() + 3600000
        )

        save(passwordResetDto)
        return SendinblueClient.sendPasswordResetEmail(user.email, token)
    }

    /**
     * Up to 7 password resets in an hour
     */
    fun isRateLimited(id: IxId<UserDto>): Boolean {
        val sent = PasswordResetEntity.count(PasswordResetTable.user eq id.toEntityId(UserTable))
        return sent >= 7
    }
}
