package app.index_it.data.sources.mongo.suggestions

import app.index_it.data.models.suggestions.NameSuggestionsDto
import app.index_it.data.sources.mongo.MongoClient
import org.litote.kmongo.Id
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

object SuggestionTaskNamesDBM {
    private val col = MongoClient.database.getCollection<NameSuggestionsDto>("suggestion-task-names")

    fun get(id: Id<NameSuggestionsDto>): NameSuggestionsDto? {
        return col.findOne(NameSuggestionsDto::id eq id)
    }
}
