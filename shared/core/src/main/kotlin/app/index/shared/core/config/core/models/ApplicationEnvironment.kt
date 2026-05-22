package app.index.shared.core.config.core.models

enum class ApplicationEnvironment {
    PRODUCTION,
    STAGING,
    LOCAL;

    val sentryName = this.name.lowercase()
}