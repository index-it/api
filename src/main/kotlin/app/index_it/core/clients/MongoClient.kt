package app.index_it.core.clients

import app.index_it.Env
import app.index_it.core.db.*
import app.index_it.core.db.lists.CategoryDBM
import app.index_it.core.db.lists.ItemDBM
import app.index_it.core.db.lists.ListDBM
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo

object MongoClient {
    private val client: MongoClient = KMongo.createClient(Env.mongo_connection_string)
    val database: MongoDatabase = client.getDatabase(Env.mongo_db_name)

    fun init() {
        CategoryDBM
        EmailVerificationDBM
        ItemDBM
        ListDBM
        NotifyDBM
        PasswordResetDBM
        TaskDBM
        UserDBM
    }

    fun close() {
        client.close()
    }
}
