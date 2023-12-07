package app.index_it.data.sources.db.core.scripts

/**
 * Script that generates the first migration for the database schema
 *
 * Results in /resources/db/migration/V1__create_db.sql
 **/
/*
fun main() {
    ConfigurationInitializer("app.index_it.config", ConfigurationReader::read).initialize()

    val DB_DRIVER = "org.postgresql.Driver"
    Database.connect(
        url = PostgresConfig.url,
        driver = DB_DRIVER,
        user = PostgresConfig.user,
        password = PostgresConfig.password
    )

    val statements = transaction {
        SchemaUtils.createStatements(
            UserTable,
            PasswordResetTable,
            EmailVerificationTable,
            ListTable,
            CategoryTable,
            ItemTable,
            ItemContentTable,
            TaskTable,
            SubTaskTable,
            NotifyTable,
            ColorSuggestionTable,
            NameSuggestionTable,
        )
    }

    println(statements)

    File("statements.txt").writeText(statements.joinToString("\n") { "$it;"} )
}
*/