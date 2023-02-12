package app.index_it.core.db

import app.index_it.core.clients.MongoClient
import app.index_it.models.user.UserDto
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*

object UserDBM {
    private val col = MongoClient.database.getCollection<UserDto>("users")

    init {
        col.ensureUniqueIndex(UserDto::email)
    }

    fun exists(email: String): Boolean {
        return col.find(UserDto::email eq email).limit(1).toList().isNotEmpty()
    }

    fun create(userDto: UserDto) {
        col.save(userDto)
    }

    fun get(id: Id<UserDto>): UserDto? {
        return col.findOne(UserDto::id eq id)
    }

    fun getFromEmail(email: String): UserDto? {
        return col.findOne(UserDto::email eq email)
    }

    fun verifyEmail(id: Id<UserDto>): UserDto? {
        return col.findOneAndUpdate(
            UserDto::id eq id,
            setValue(UserDto::email_verified, true),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun delete(id: Id<UserDto>) {
        col.deleteOne(UserDto::id eq id)
    }
}
