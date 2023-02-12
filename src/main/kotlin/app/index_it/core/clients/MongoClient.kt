package app.index_it.core.clients

import app.index_it.Env
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule
import org.litote.kmongo.serialization.registerModule

object MongoClient {
    private val client: MongoClient = KMongo.createClient(Env.mongo_connection_string)
    val database: MongoDatabase = client.getDatabase(Env.mongo_db_name)

    fun close() {
        client.close()
    }
}
