package app.index_it.core.db

import app.index_it.core.clients.MongoClient
import app.index_it.models.user.UserDto
import org.bson.types.ObjectId
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.id.toId

object UserDBM {
    private val col = MongoClient.database.getCollection<UserDto>("users")

    fun getUser(id: String): UserDto? {
        return col.findOne(UserDto::_id eq ObjectId(id).toId())
    }
}
