package app.index.api.config

import app.index.api.config.core.Configuration
import app.index.api.config.core.ConfigurationProperty

@Configuration("apple")
object AppleConfig {
    @ConfigurationProperty("bundle.id")
    var bundleId: String = "app.index-it.index"
}
