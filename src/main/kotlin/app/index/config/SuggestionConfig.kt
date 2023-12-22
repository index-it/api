package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

@Configuration("suggestion")
object SuggestionConfig {
    @ConfigurationProperty("list.names.id")
    var suggestionListNamesId: Int = 1

    @ConfigurationProperty("category.names.id")
    var suggestionCategoryNamesId: Int = 2

    @ConfigurationProperty("item.names.id")
    var suggestionItemNamesId: Int = 3

    @ConfigurationProperty("task.names.id")
    var suggestionTaskNamesId: Int = 4

    @ConfigurationProperty("colors.id")
    var suggestionColorsId: Int = 1
}
