package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

@Configuration("pro")
object ProConfig {
    @ConfigurationProperty("bypass")
    var bypass: Boolean = false
}
