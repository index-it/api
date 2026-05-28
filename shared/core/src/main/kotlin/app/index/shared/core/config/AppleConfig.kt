package app.index.shared.core.config

import app.index.shared.core.config.core.Configuration
import app.index.shared.core.config.core.ConfigurationProperty

@Configuration("apple")
object AppleConfig {
    @ConfigurationProperty("bundle.id")
    var bundleId: String = "app.index-it.index"
}
