package app.index_it.core.db

import app.index_it.core.clients.MongoClient
import app.index_it.models.suggestions.ListNameSuggestionsDto
import org.litote.kmongo.Id
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

object SuggestionListNamesDBM {
    private val col = MongoClient.database.getCollection<ListNameSuggestionsDto>("suggestion-list-names")

    fun get(id: Id<ListNameSuggestionsDto>): ListNameSuggestionsDto? {
        return col.findOne(ListNameSuggestionsDto::id eq id)
    }
}
