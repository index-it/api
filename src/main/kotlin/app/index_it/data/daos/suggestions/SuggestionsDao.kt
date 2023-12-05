package app.index_it.data.daos.suggestions

import app.index_it.config.SuggestionConfig
import app.index_it.core.logic.typedId.impl.IxIntId
import app.index_it.core.logic.typedId.toIxIntId
import app.index_it.data.models.suggestions.ColorSuggestionsDto
import app.index_it.data.models.suggestions.NameSuggestionsDto
import app.index_it.data.sources.cache.cm.suggestions.*
import app.index_it.data.sources.db.dbi.suggestion.impl.SuggestionColorsDBIImpl
import app.index_it.data.sources.db.dbi.suggestion.impl.SuggestionNamesDBIImpl
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {  }

object SuggestionsDao {
    private val listNameSuggestionsId: IxIntId<NameSuggestionsDto> = SuggestionConfig.suggestionListNamesId.toIxIntId()
    private val categoryNameSuggestionsId: IxIntId<NameSuggestionsDto> = SuggestionConfig.suggestionCategoryNamesId.toIxIntId()
    private val itemNameSuggestionsId: IxIntId<NameSuggestionsDto> = SuggestionConfig.suggestionItemNamesId.toIxIntId()
    private val taskNameSuggestionsId: IxIntId<NameSuggestionsDto> = SuggestionConfig.suggestionTaskNamesId.toIxIntId()

    private val colorSuggestionsId: IxIntId<ColorSuggestionsDto> = SuggestionConfig.suggestionColorsId.toIxIntId()

    fun getRandomNameSuggestion(nameSuggestionsDto: NameSuggestionsDto?): String {
        return nameSuggestionsDto?.names?.randomOrNull() ?: run {
            logger.warn { "Empty array of suggested names in suggestion with id $listNameSuggestionsId" }
            "Quack"
        }
    }

    suspend fun getRandomColor(): String {
        val colors = getColors() ?: return "#000000"

        return colors.colors.randomOrNull() ?: run {
            logger.warn { "Empty array of template list colors in template with id $colorSuggestionsId" }
            "#FFFFFF"
        }
    }

    suspend fun getListNames() : NameSuggestionsDto? {
        var names = SuggestionListNamesCM.get(listNameSuggestionsId)

        if (names == null) {
            names = SuggestionNamesDBIImpl.get(listNameSuggestionsId)
                ?: run {
                    logger.warn { "No template for list names found with id $listNameSuggestionsId" }
                    return null
                }

            SuggestionListNamesCM.cache(names)
        }

        return names
    }

    suspend fun getCategoryNames() : NameSuggestionsDto? {
        var names = SuggestionCategoryNamesCM.get(categoryNameSuggestionsId)

        if (names == null) {
            names = SuggestionNamesDBIImpl.get(categoryNameSuggestionsId)
                ?: run {
                    logger.warn { "No template for category names found with id $categoryNameSuggestionsId" }
                    return null
                }

            SuggestionCategoryNamesCM.cache(names)
        }

        return names
    }

    suspend fun getItemNames() : NameSuggestionsDto? {
        var names = SuggestionItemNamesCM.get(itemNameSuggestionsId)

        if (names == null) {
            names = SuggestionNamesDBIImpl.get(itemNameSuggestionsId)
                ?: run {
                    logger.warn { "No template for item names found with id $itemNameSuggestionsId" }
                    return null
                }

            SuggestionItemNamesCM.cache(names)
        }

        return names
    }

    suspend fun getTaskNames() : NameSuggestionsDto? {
        var names = SuggestionTaskNamesCM.get(taskNameSuggestionsId)

        if (names == null) {
            names = SuggestionNamesDBIImpl.get(taskNameSuggestionsId)
                    ?: run {
                        logger.warn { "No template for task names found with id $taskNameSuggestionsId" }
                        return null
                    }

            SuggestionTaskNamesCM.cache(names)
        }

        return names
    }

    suspend fun getColors() : ColorSuggestionsDto? {
        var colors = SuggestionColorsCM.get(colorSuggestionsId)

        if (colors == null) {
            colors = SuggestionColorsDBIImpl.get(colorSuggestionsId)
                ?: run {
                    logger.warn { "No template for list colors found with id $colorSuggestionsId" }
                    return null
                }

            SuggestionColorsCM.cache(colors)
        }

        return colors
    }
}
