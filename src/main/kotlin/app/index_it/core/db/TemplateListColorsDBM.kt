package app.index_it.core.db

import app.index_it.core.clients.MongoClient
import app.index_it.models.templates.ListColorsDto
import org.litote.kmongo.Id
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

object TemplateListColorsDBM {
    private val col = MongoClient.database.getCollection<ListColorsDto>("template-list-colors")

    fun get(id: Id<ListColorsDto>): ListColorsDto? {
        return col.findOne(ListColorsDto::id eq id)
    }
}
