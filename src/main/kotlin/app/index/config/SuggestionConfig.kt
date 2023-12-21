package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

@Configuration("suggestion")
object SuggestionConfig {
    @ConfigurationProperty("list.names.id")
    var suggestionListNamesId: Int = 0

    @ConfigurationProperty("category.names.id")
    var suggestionCategoryNamesId: Int = 0

    @ConfigurationProperty("item.names.id")
    var suggestionItemNamesId: Int = 0

    @ConfigurationProperty("task.names.id")
    var suggestionTaskNamesId: Int = 0

    @ConfigurationProperty("colors.id")
    var suggestionColorsId: Int = 0
}
