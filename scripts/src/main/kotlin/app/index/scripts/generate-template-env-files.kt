package app.index.scripts
import app.index.api.config.core.ConfigurationManager
import app.index.api.config.core.ConfigurationReader
import createScriptOutputsFolderIfNotExisting
import java.io.File

fun main() {
    val configs = ConfigurationManager(
        packageName = ConfigurationManager.DEFAULT_CONFIG_PACKAGE,
        configurationReader = ConfigurationReader::read
    ).listConfigurations()

    val folder = createScriptOutputsFolderIfNotExisting()
    val file = File(folder, ".env.template")
    file.writeText(configs.joinToString("\n") { it.toString() } )
}