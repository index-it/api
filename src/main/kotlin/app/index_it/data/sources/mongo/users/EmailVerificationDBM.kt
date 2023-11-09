package app.index_it.data.sources.db

import app.index_it.core.clients.MongoClient
import app.index_it.models.email.EmailVerificationDto
import app.index_it.models.user.UserDto
import com.mongodb.client.model.IndexOptions
import org.litote.kmongo.*
import java.util.concurrent.TimeUnit

object EmailVerificationDBM {
    private val col = MongoClient.database.getCollection<EmailVerificationDto>("email-verification-tokens")

    init {
        col.ensureUniqueIndex(EmailVerificationDto::token)
        col.ensureIndex(EmailVerificationDto::userId)
        // Make verification codes automatically expire
        col.ensureIndex(
            EmailVerificationDto::expireAt,
            indexOptions = IndexOptions().expireAfter(0, TimeUnit.SECONDS)
        )
    }

    fun countSaved(id: Id<UserDto>): Int {
        return col.find(EmailVerificationDto::userId eq id).count()
    }

    fun save(emailVerificationDto: EmailVerificationDto) {
        col.save(emailVerificationDto)
    }

    fun get(token: String): EmailVerificationDto? {
        return col.findOne(EmailVerificationDto::token eq token)
    }

    fun deleteAll(id: Id<UserDto>) {
        col.deleteMany(EmailVerificationDto::userId eq id)
    }
}
