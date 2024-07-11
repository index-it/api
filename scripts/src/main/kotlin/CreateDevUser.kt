import app.index.config.PostgresConfig
import app.index.config.core.ConfigurationManager
import app.index.config.core.ConfigurationReader
import app.index.core.logic.DatetimeUtils
import app.index.core.logic.PasswordEncoder
import app.index.core.logic.typedId.newIxId
import app.index.data.models.user.UserData
import app.index.data.sources.db.dbi.user.impl.UserDBIImpl
import org.jetbrains.exposed.sql.Database

private const val DB_DRIVER = "org.postgresql.Driver"

suspend fun main() {
    ConfigurationManager(ConfigurationManager.DEFAULT_CONFIG_PACKAGE, ConfigurationReader::read).initialize()

    Database.connect(
        url = PostgresConfig.url,
        driver = DB_DRIVER,
        user = PostgresConfig.user,
        password = PostgresConfig.password
    )

    val devUser = UserData(
        id = newIxId(),
        email = "giuliopime@gmail.com",
        passwordHash = PasswordEncoder().encode("Password1!"),
        emailVerified = true,
        creationTimestamp = DatetimeUtils.currentMillis(),
        creationSource = UserData.CreationSource.NONE,
        has_pro = false
    )

    try {
        UserDBIImpl().create(devUser)
        println("created dev user - email: giuliopime@gmail.com - password: Password1!")
    } catch (e: Exception) {
        println("failed creating dev user: $e")
    }
}