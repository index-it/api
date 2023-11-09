package app.index_it.data.sources.mongo

import app.index_it.Env
import app.index_it.data.sources.mongo.lists.CategoryDBM
import app.index_it.data.sources.mongo.lists.ItemDBM
import app.index_it.data.sources.mongo.lists.ListDBM
import app.index_it.data.sources.mongo.tasks.TaskDBM
import app.index_it.data.sources.mongo.users.EmailVerificationDBM
import app.index_it.data.sources.mongo.users.NotifyDBM
import app.index_it.data.sources.mongo.users.PasswordResetDBM
import app.index_it.data.sources.mongo.users.UserDBM
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
