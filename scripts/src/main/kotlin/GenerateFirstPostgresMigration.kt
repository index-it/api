import app.index_it.config.PostgresConfig
import app.index_it.config.core.ConfigurationManager
import app.index_it.config.core.ConfigurationReader
import app.index_it.data.sources.db.schemas.lists.CategoryTable
import app.index_it.data.sources.db.schemas.lists.ItemContentTable
import app.index_it.data.sources.db.schemas.lists.ItemTable
import app.index_it.data.sources.db.schemas.lists.ListTable
import app.index_it.data.sources.db.schemas.suggestions.ColorSuggestionTable
import app.index_it.data.sources.db.schemas.suggestions.NameSuggestionTable
import app.index_it.data.sources.db.schemas.tasks.SubTaskTable
import app.index_it.data.sources.db.schemas.tasks.TaskTable
import app.index_it.data.sources.db.schemas.user.EmailVerificationTable
import app.index_it.data.sources.db.schemas.user.PasswordResetTable
import app.index_it.data.sources.db.schemas.user.UsersTable
import app.index_it.data.sources.db.schemas.web.ReleaseNotifyTable
import core.createScriptOutputsFolderIfNotExisting
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

private const val DB_DRIVER = "org.postgresql.Driver"

/**
 * Script that generates the first migration for the database schema
 * Result file should be put in /resources/db/migration/V1__create_db.sql
 */
fun main() {
    ConfigurationManager("app.index_it.config", ConfigurationReader::read).initialize()

    Database.connect(
        url = PostgresConfig.url,
        driver = DB_DRIVER,
        user = PostgresConfig.user,
        password = PostgresConfig.password
    )

    val statements = transaction {
        SchemaUtils.createStatements(
            UsersTable,
            PasswordResetTable,
            EmailVerificationTable,
            ListTable,
            CategoryTable,
            ItemTable,
            ItemContentTable,
            TaskTable,
            SubTaskTable,
            ReleaseNotifyTable,
            ColorSuggestionTable,
            NameSuggestionTable,
        )
    }

    val folder = createScriptOutputsFolderIfNotExisting()
    val file = File(folder, "V1__create_db.sql")
    file.writeText(statements.joinToString("\n") { "$it;"} )
}