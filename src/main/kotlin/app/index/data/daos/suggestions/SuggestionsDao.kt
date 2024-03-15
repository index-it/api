package app.index.data.daos.suggestions

import app.index.config.SuggestionConfig
import app.index.core.logic.typedId.impl.IxIntId
import app.index.core.logic.typedId.toIxIntId
import app.index.data.models.suggestions.ColorSuggestionsData
import app.index.data.models.suggestions.NameSuggestionsData
import app.index.data.sources.cache.cm.suggestions.SuggestionCategoryNamesCM
import app.index.data.sources.cache.cm.suggestions.SuggestionColorsCM
import app.index.data.sources.cache.cm.suggestions.SuggestionItemNamesCM
import app.index.data.sources.cache.cm.suggestions.SuggestionListNamesCM
import app.index.data.sources.db.dbi.suggestion.SuggestionColorsDBI
import app.index.data.sources.db.dbi.suggestion.SuggestionNamesDBI
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger { }

@Single(createdAtStart = true)
class SuggestionsDao(
    private val suggestionNamesDBI: SuggestionNamesDBI,
    private val suggestionColorsDBI: SuggestionColorsDBI,
    private val suggestionListNamesCM: SuggestionListNamesCM,
    private val suggestionCategoryNamesCM: SuggestionCategoryNamesCM,
    private val suggestionItemNamesCM: SuggestionItemNamesCM,
    private val suggestionTaskNamesCM: SuggestionItemNamesCM,
    private val suggestionColorsCM: SuggestionColorsCM,
) {
    private val listNameSuggestionsId: IxIntId<NameSuggestionsData> = SuggestionConfig.suggestionListNamesId.toIxIntId()
    private val categoryNameSuggestionsId: IxIntId<NameSuggestionsData> = SuggestionConfig.suggestionCategoryNamesId.toIxIntId()
    private val itemNameSuggestionsId: IxIntId<NameSuggestionsData> = SuggestionConfig.suggestionItemNamesId.toIxIntId()
    private val taskNameSuggestionsId: IxIntId<NameSuggestionsData> = SuggestionConfig.suggestionTaskNamesId.toIxIntId()

    private val colorSuggestionsId: IxIntId<ColorSuggestionsData> = SuggestionConfig.suggestionColorsId.toIxIntId()

    fun getRandomNameSuggestion(nameSuggestionsData: NameSuggestionsData?): String {
        return nameSuggestionsData?.names?.randomOrNull() ?: run {
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

    suspend fun getListNames(locale: String): NameSuggestionsData? {
        var names = suggestionListNamesCM.get(listNameSuggestionsId, locale)

        if (names == null) {
            names = suggestionNamesDBI.get(listNameSuggestionsId, locale)
                ?: run {
                    logger.warn { "No template for list names found with id $listNameSuggestionsId" }
                    return null
                }

            suggestionListNamesCM.cache(names)
        }

        return names
    }

    suspend fun getCategoryNames(locale: String): NameSuggestionsData? {
        var names = suggestionCategoryNamesCM.get(categoryNameSuggestionsId, locale)

        if (names == null) {
            names = suggestionNamesDBI.get(categoryNameSuggestionsId, locale)
                ?: run {
                    logger.warn { "No template for category names found with id $categoryNameSuggestionsId" }
                    return null
                }

            suggestionCategoryNamesCM.cache(names)
        }

        return names
    }

    suspend fun getItemNames(locale: String): NameSuggestionsData? {
        var names = suggestionItemNamesCM.get(itemNameSuggestionsId, locale)

        if (names == null) {
            names = suggestionNamesDBI.get(itemNameSuggestionsId, locale)
                ?: run {
                    logger.warn { "No template for item names found with id $itemNameSuggestionsId" }
                    return null
                }

            suggestionItemNamesCM.cache(names)
        }

        return names
    }

    suspend fun getTaskNames(locale: String): NameSuggestionsData? {
        var names = suggestionTaskNamesCM.get(taskNameSuggestionsId, locale)

        if (names == null) {
            names = suggestionNamesDBI.get(taskNameSuggestionsId, locale)
                ?: run {
                    logger.warn { "No template for task names found with id $taskNameSuggestionsId" }
                    return null
                }

            suggestionTaskNamesCM.cache(names)
        }

        return names
    }

    suspend fun getColors(): ColorSuggestionsData? {
        var colors = suggestionColorsCM.get(colorSuggestionsId)

        if (colors == null) {
            colors = suggestionColorsDBI.get(colorSuggestionsId)
                ?: run {
                    logger.warn { "No template for list colors found with id $colorSuggestionsId" }
                    return null
                }

            suggestionColorsCM.cache(colors)
        }

        return colors
    }
}
