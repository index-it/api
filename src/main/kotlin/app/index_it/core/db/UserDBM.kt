package app.index_it.core.db

import app.index_it.core.clients.MongoClient
import app.index_it.models.user.ClientUserDto
import app.index_it.models.user.UserDto
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*

object UserDBM {
    private val col = MongoClient.database.getCollection<UserDto>("users")

    init {
        col.ensureUniqueIndex(UserDto::email)
    }

    fun get(id: String): UserDto? {
        return col.findOne(UserDto::id eq id)
    }

    fun getFromEmail(email: String): UserDto? {
        return col.findOne(UserDto::email eq email)
    }

    fun update(id: String, clientUserDto: ClientUserDto): UserDto? {
        return col.findOneAndUpdate(
            UserDto::id eq id,
            setValue(UserDto::name, clientUserDto.name),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun delete(id: String) {
        col.deleteOne(UserDto::id eq id)
    }
}
