package app.index.shared.core.config

import app.index.shared.core.config.core.Configuration
import app.index.shared.core.config.core.ConfigurationProperty

@Configuration("pro")
object ProConfig {
    @ConfigurationProperty("bypass")
    var bypass: Boolean = false
}
