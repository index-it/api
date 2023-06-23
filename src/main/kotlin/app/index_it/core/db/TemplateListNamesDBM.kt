package app.index_it.core.db

import app.index_it.core.clients.MongoClient
import app.index_it.models.templates.ListNamesDto
import org.litote.kmongo.Id
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

object TemplateListNamesDBM {
    private val col = MongoClient.database.getCollection<ListNamesDto>("template_list_names")

    fun get(id: Id<ListNamesDto>): ListNamesDto? {
        return col.findOne(ListNamesDto::id eq id)
    }
}
