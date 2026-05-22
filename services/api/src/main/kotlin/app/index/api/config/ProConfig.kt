package app.index.api.config

import app.index.api.config.core.Configuration
import app.index.api.config.core.ConfigurationProperty

@Configuration("pro")
object ProConfig {
    @ConfigurationProperty("bypass")
    var bypass: Boolean = false
}
