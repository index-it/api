package app.index.config.core

@Target(AnnotationTarget.CLASS)
annotation class Configuration(
    val prefix: String,
)

@Target(AnnotationTarget.PROPERTY)
annotation class ConfigurationProperty(
    val name: String,
    val optional: Boolean = false,
)
