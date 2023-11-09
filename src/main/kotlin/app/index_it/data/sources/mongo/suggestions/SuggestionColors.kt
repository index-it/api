package app.index_it.data.sources.mongo.suggestions

import app.index_it.data.sources.mongo.MongoClient
import app.index_it.data.models.suggestions.ColorSuggestionsDto
import org.litote.kmongo.Id
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

object SuggestionColors {
    private val col = MongoClient.database.getCollection<ColorSuggestionsDto>("suggestion-colors")

    fun get(id: Id<ColorSuggestionsDto>): ColorSuggestionsDto? {
        return col.findOne(ColorSuggestionsDto::id eq id)
    }
}
