package app.index_it.core.clients

import app.index_it.Env
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo

object MongoClient {
    private val client = KMongo.createClient(Env.mongo_connection_string)
    val database: MongoDatabase = client.getDatabase(Env.mongo_db_name)
}
