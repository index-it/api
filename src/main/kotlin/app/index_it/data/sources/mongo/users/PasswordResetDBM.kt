package app.index_it.data.sources.mongo.users

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.PasswordResetDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.mongo.MongoClient
import com.mongodb.client.model.IndexOptions
import org.litote.kmongo.*
import java.util.concurrent.TimeUnit

object PasswordResetDBM {
    private val col = MongoClient.database.getCollection<PasswordResetDto>("password-reset-tokens")

    init {
        col.ensureUniqueIndex(PasswordResetDto::token)
        col.ensureIndex(PasswordResetDto::userId)
        // Make password reset tokens automatically expire
        col.ensureIndex(
            PasswordResetDto::expireAt,
            indexOptions = IndexOptions().expireAfter(0, TimeUnit.SECONDS)
        )
    }

    fun countSaved(id: IxId<UserDto>): Int {
        return col.find(PasswordResetDto::userId eq id).count()
    }

    fun save(passwordResetDto: PasswordResetDto) {
        col.save(passwordResetDto)
    }

    fun get(token: String): PasswordResetDto? {
        return col.findOne(PasswordResetDto::token eq token)
    }

    fun deleteAll(id: IxId<UserDto>) {
        col.deleteMany(PasswordResetDto::userId eq id)
    }
}
