package app.index_it.core.db

import app.index_it.core.clients.MongoClient
import app.index_it.models.user.PasswordResetDto
import app.index_it.models.user.UserDto
import com.mongodb.client.model.IndexOptions
import org.litote.kmongo.*
import java.util.concurrent.TimeUnit

object PasswordResetDBM {
    private val col = MongoClient.database.getCollection<PasswordResetDto>("password-reset-tokens")

    init {
        col.ensureUniqueIndex(PasswordResetDto::token)
        col.ensureIndex(PasswordResetDto::user_id)
        // Make password reset tokens automatically expire
        col.ensureIndex(
            PasswordResetDto::expire_at,
            indexOptions = IndexOptions().expireAfter(0, TimeUnit.SECONDS)
        )
    }

    fun countSaved(id: Id<UserDto>): Int {
        return col.find(PasswordResetDto::user_id eq id).count()
    }

    fun save(passwordResetDto: PasswordResetDto) {
        col.save(passwordResetDto)
    }

    fun get(token: String): PasswordResetDto? {
        return col.findOne(PasswordResetDto::token eq token)
    }

    fun deleteAll(id: Id<UserDto>) {
        col.deleteMany(PasswordResetDto::user_id eq id)
    }
}
