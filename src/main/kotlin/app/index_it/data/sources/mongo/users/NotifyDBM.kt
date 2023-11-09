package app.index_it.data.sources.mongo.users

import app.index_it.data.models.web.NotifyDto
import app.index_it.data.sources.mongo.MongoClient
import org.litote.kmongo.ensureUniqueIndex
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

object NotifyDBM {
    private val col = MongoClient.database.getCollection<NotifyDto>("notify")

    init {
        col.ensureUniqueIndex(NotifyDto::email)
    }

    fun notify(email: String) {
        if(
            col.findOne(NotifyDto::email eq email) == null
        )
            col.insertOne(NotifyDto(email))
    }
}
