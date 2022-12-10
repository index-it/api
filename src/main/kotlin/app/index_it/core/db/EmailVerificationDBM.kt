package app.index_it.core.db

import app.index_it.core.clients.MongoClient
import app.index_it.models.email.EmailVerificationDto
import com.mongodb.client.model.IndexOptions
import org.litote.kmongo.*
import java.util.concurrent.TimeUnit

object EmailVerificationDBM {
    private val col = MongoClient.database.getCollection<EmailVerificationDto>("email-verification")

    init {
        col.ensureUniqueIndex(EmailVerificationDto::code)
        col.ensureIndex(EmailVerificationDto::user_email)
        // Make verification codes automatically expire
        col.ensureIndex(
            EmailVerificationDto::expire_at,
            indexOptions = IndexOptions().expireAfter(0, TimeUnit.SECONDS)
        )
    }

    fun countSaved(email: String): Int {
        return col.find(EmailVerificationDto::user_email eq email).count()
    }

    fun save(emailVerificationDto: EmailVerificationDto) {
        col.save(emailVerificationDto)
    }

    fun get(code: String): EmailVerificationDto? {
        return col.findOne(EmailVerificationDto::code eq code)
    }

    fun delete(code: String) {
        col.deleteOne(EmailVerificationDto::code eq code)
    }
}
