package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

@Configuration("apple")
object AppleConfig {
    @ConfigurationProperty("bundle.id")
    var bundleId: String = "app.index-it.index"
}
