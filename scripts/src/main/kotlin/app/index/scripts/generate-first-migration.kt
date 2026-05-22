package app.index.scripts

import app.index.shared.core.config.PostgresConfig
import app.index.shared.core.config.core.ConfigurationManager
import app.index.shared.core.config.core.ConfigurationReader
import app.index.api.data.sources.db.schemas.lists.CategoryTable
import app.index.api.data.sources.db.schemas.lists.ItemContentTable
import app.index.api.data.sources.db.schemas.lists.ItemTable
import app.index.api.data.sources.db.schemas.lists.ListEditorTable
import app.index.api.data.sources.db.schemas.lists.ListInviteTable
import app.index.api.data.sources.db.schemas.lists.ListTable
import app.index.api.data.sources.db.schemas.lists.ListUserInviteTable
import app.index.api.data.sources.db.schemas.lists.ListViewerTable
import app.index.api.data.sources.db.schemas.tasks.SubTaskTable
import app.index.api.data.sources.db.schemas.tasks.TaskReminderJobTable
import app.index.api.data.sources.db.schemas.tasks.TaskReminderTable
import app.index.api.data.sources.db.schemas.tasks.TaskTable
import app.index.api.data.sources.db.schemas.user.EmailVerificationTable
import app.index.api.data.sources.db.schemas.user.FCMRegistrationTokenTable
import app.index.api.data.sources.db.schemas.user.PasswordResetTable
import app.index.api.data.sources.db.schemas.user.UsersTable
import app.index.api.data.sources.db.schemas.web.ReleaseNotifyTable
import createScriptOutputsFolderIfNotExisting
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
    ConfigurationManager(ConfigurationManager.DEFAULT_CONFIG_PACKAGE, ConfigurationReader::read).initialize()

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
            ListViewerTable,
            ListEditorTable,
            ListUserInviteTable,
            ListInviteTable,
            CategoryTable,
            ItemTable,
            ItemContentTable,
            TaskTable,
            TaskReminderTable,
            TaskReminderJobTable,
            SubTaskTable,
            ReleaseNotifyTable,
            FCMRegistrationTokenTable
        )
    }

    val folder = createScriptOutputsFolderIfNotExisting()
    val file = File(folder, "V1__create_db.sql")
    file.writeText(statements.joinToString("\n") { "$it;"} )
}
