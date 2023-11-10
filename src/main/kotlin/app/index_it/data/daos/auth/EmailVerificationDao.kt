package app.index_it.data.daos.auth

import app.index_it.core.clients.SendinblueClient
import app.index_it.core.logic.TokenGenerator
import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.email.EmailVerificationDto
import app.index_it.data.models.email.fromDto
import app.index_it.data.models.email.toDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.db.schemas.user.EmailVerificationEntity
import app.index_it.data.sources.db.schemas.user.EmailVerificationTable
import app.index_it.data.sources.db.schemas.user.UserTable
import app.index_it.data.sources.db.toEntityId
import io.ktor.util.date.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

object EmailVerificationDao {
    private fun save(emailVerificationDto: EmailVerificationDto) =
        transaction {
            EmailVerificationEntity.new {
                fromDto(emailVerificationDto)
            }
        }


    fun get(token: String): EmailVerificationDto? {
        return EmailVerificationEntity
            .find { EmailVerificationTable.token eq TokenGenerator.hashToken(token) }
            .limit(1)
            .firstOrNull()
            ?.toDto()
    }

    /**
     * Sends a verification email to the provided email
     * and returns true if the email was sent successfully, false otherwise
     */
    suspend fun createAndSend(user: UserDto): Boolean {
        val (token, hashedToken) = TokenGenerator.generate()

        val emailVerificationDto = EmailVerificationDto(
            token = hashedToken,
            userId = user.id,
            expireAt = getTimeMillis() + 3600000
        )

        save(emailVerificationDto)
        return SendinblueClient.sendEmailVerificationEmail(user.email, token)
    }

    /**
     * Deletes all email verification tokens of a specific email.
     */
    fun deleteAll(id: IxId<UserDto>) = EmailVerificationTable.deleteWhere { user eq id.toEntityId(UserTable) }

    fun isRateLimited(id: IxId<UserDto>): Boolean {
        val sent = EmailVerificationEntity.count(EmailVerificationTable.user eq id.toEntityId(UserTable))
        return sent >= 5
    }
}
