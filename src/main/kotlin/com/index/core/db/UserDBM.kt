package com.index.core.db

import com.index.core.clients.MongoClient
import com.index.models.user.UserDto
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
