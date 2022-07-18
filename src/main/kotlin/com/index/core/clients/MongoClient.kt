package com.index.core.clients

import com.index.Env
import com.index.models.user.UserDto
import com.mongodb.client.MongoDatabase
import org.bson.types.ObjectId
import org.litote.kmongo.KMongo
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.id.toId

object MongoClient {
    private val client = KMongo.createClient(Env.mongo_connection_string)
    val database: MongoDatabase = client.getDatabase(Env.mongo_db_name)
}
