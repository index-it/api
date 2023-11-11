package app.index_it.data.sources.mongo.suggestions

import app.index_it.core.logic.typedId.impl.IxIntId
import app.index_it.data.models.suggestions.NameSuggestionsDto
import app.index_it.data.sources.mongo.MongoClient
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

object SuggestionCategoryNamesDBM {
    private val col = MongoClient.database.getCollection<NameSuggestionsDto>("suggestion-category-names")

    fun get(id: IxIntId<NameSuggestionsDto>): NameSuggestionsDto? {
        return col.findOne(NameSuggestionsDto::id eq id)
    }
}
