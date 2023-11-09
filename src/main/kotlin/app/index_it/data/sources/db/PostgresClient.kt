package app.index_it.data.sources.db

import app.index_it.Env
import app.index_it.data.models.suggestions.NameTable
import app.index_it.data.models.web.NotifyTable
import app.index_it.data.sources.db.schemas.lists.CategoryTable
import app.index_it.data.sources.db.schemas.lists.ItemContentTable
import app.index_it.data.sources.db.schemas.lists.ItemTable
import app.index_it.data.sources.db.schemas.lists.ListTable
import app.index_it.data.sources.db.schemas.suggestions.ColorSuggestionTable
import app.index_it.data.sources.db.schemas.suggestions.ColorTable
import app.index_it.data.sources.db.schemas.suggestions.NameSuggestionTable
import app.index_it.data.sources.db.schemas.tasks.TaskTable
import app.index_it.data.sources.db.schemas.user.EmailVerificationTable
import app.index_it.data.sources.db.schemas.user.PasswordResetTable
import app.index_it.data.sources.db.schemas.user.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils

object PostgresClient {
    private val database = Database.connect(
        url = Env.postgres_url,
        driver = "org.postgresql.Driver",
        user = Env.postgres_user,
        password = Env.postgres_password
    )

    init {
        SchemaUtils.create(
            NotifyTable,

            UserTable,
            EmailVerificationTable,
            PasswordResetTable,

            ColorSuggestionTable,
            ColorTable,
            NameSuggestionTable,
            NameTable,

            ListTable,
            CategoryTable,
            ItemTable,
            ItemContentTable,

            TaskTable
        )
    }

    fun getDb() = database
}