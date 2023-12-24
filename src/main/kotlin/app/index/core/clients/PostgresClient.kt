package app.index.core.clients

import app.index.config.PostgresConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.annotation.Single

private const val DB_DRIVER = "org.postgresql.Driver"

private val log = KotlinLogging.logger { }

@Single(createdAtStart = true)
class PostgresClient {
    @Suppress("UNUSED")
    private val database = Database.connect(
        url = PostgresConfig.url,
        driver = DB_DRIVER,
        user = PostgresConfig.user,
        password = PostgresConfig.password,
    )

    @Suppress("UNUSED")
    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(
            context = Dispatchers.IO,
        ) { block() }

    init {
        runMigrations()
    }

    private fun runMigrations() {
        val flyway = Flyway.configure()
            .driver(DB_DRIVER)
            .dataSource(PostgresConfig.url, PostgresConfig.user, PostgresConfig.password)
            .validateMigrationNaming(true)
            .load()
        try {
            flyway.info()
            flyway.migrate()
            log.info { "Flyway migration has finished" }
        } catch (e: Exception) {
            log.error(e) { "Exception running flyway migration" }
            throw e
        }
    }

    // TODO: Suggestions

    /*
    private suspend fun setupColorSuggestions() {
        dbQuery {
            if (SuggestionColorsDBIImpl.get(DEFAULT_COLOR_SUGGESTIONS_ID.toIxIntId()) == null) {
                val defaultColors = arrayOf(
                    "#FB9079",
                    "#14704A",
                    "#B64B9E",
                    "#DF821A",
                    "#4C7677",
                    "#418FB6",
                    "#15846D",
                    "#A51D1A",
                    "#3A5574",
                    "#3F3488",
                    "#6A8124",
                    "#CF9E34",
                    "#FBA592",
                    "#111111",
                    "#FFFFFF"
                )

                ColorSuggestionEntity.new(DEFAULT_COLOR_SUGGESTIONS_ID) {
                    description = "Default colors"
                    colors = defaultColors
                }
            }
        }
    }

    private suspend fun setupListNameSuggestions() {
        dbQuery {
            if (SuggestionNamesDBIImpl.get(DEFAULT_LIST_NAME_SUGGESTIONS_ID.toIxIntId()) == null) {
                val defaultNames = arrayOf(
                    "Movie Mania",
                    "Pop Culture",
                    "Wanderlust Wonders",
                    "Melody Mix",
                    "Epic Eats",
                    "Comedy Corner",
                    "Urban Vibes",
                    "Artistic Antics",
                    "Retro Rewind",
                    "Majestic Peaks",
                    "Serene Sounds",
                    "Foodie Finds",
                    "Action Flicks",
                    "Quirky Quotients",
                    "Festive Feasts",
                    "Serendipity Soirees",
                    "Timeless Tunes",
                    "Whimsical Wanderings",
                    "Picturesque Paradises",
                    "Lyrical Laughter"
                )

                NameSuggestionEntity.new(DEFAULT_LIST_NAME_SUGGESTIONS_ID) {
                    description = "Default list names"
                    names = defaultNames
                }
            }
        }
    }

    private suspend fun setupCategoryNameSuggestions() {
        dbQuery {
            if (SuggestionNamesDBIImpl.get(DEFAULT_CATEGORY_NAME_SUGGESTIONS_ID.toIxIntId()) == null) {
                val defaultNames = arrayOf(
                    "Completed Conquests",
                    "To-Do Treasures",
                    "In Progress Pioneers",
                    "Pending Pursuits",
                    "Achieved Ambitions",
                    "Ongoing Odysseys",
                    "Checked-off Challenges",
                    "Unfinished Ventures",
                    "Task Trailblazing",
                    "In the Pipeline",
                    "Triumphs in the Making",
                    "Tasks at Hand",
                    "Active Endeavors",
                    "Onward March",
                    "Upcoming Exploits",
                    "Underway Assignments",
                    "Steady Strides",
                    "Work in Motion",
                    "Tasks on Track",
                    "Tasks on Hold"
                )

                NameSuggestionEntity.new(DEFAULT_CATEGORY_NAME_SUGGESTIONS_ID) {
                    description = "Default category names"
                    names = defaultNames
                }
            }
        }
    }

    private suspend fun setupItemNameSuggestions() {
        dbQuery {
            if (SuggestionNamesDBIImpl.get(DEFAULT_ITEM_NAME_SUGGESTIONS_ID.toIxIntId()) == null) {
                val defaultNames = arrayOf(
                    "Skydiving Over Peaks",
                    "Royal Castle Tour",
                    "Desert Star Camping",
                    "Arctic Cruise",
                    "Rainforest Trek",
                    "Canyon Rafting",
                    "Alpine Hike",
                    "Temple Exploration",
                    "Coral Reef Dive",
                    "Safari Adventure",
                    "Gondola Ride",
                    "Mountain Helicopter",
                    "Opera Night",
                    "Volcano Summit",
                    "Dolphin Swim",
                    "Northern Lights",
                    "Tea Ceremony",
                    "Savanna Safari",
                    "Horseback Voyage",
                    "Ballroom Masquerade"
                )

                NameSuggestionEntity.new(DEFAULT_ITEM_NAME_SUGGESTIONS_ID) {
                    description = "Default item names"
                    names = defaultNames
                }
            }
        }
    }

    private suspend fun setupTaskNameSuggestions() {
        dbQuery {
            if (SuggestionNamesDBIImpl.get(DEFAULT_TASK_NAME_SUGGESTIONS_ID.toIxIntId()) == null) {
                val defaultNames = arrayOf(
                    "Cave exploration",
                    "Hot air ballooning",
                    "Food truck hopping",
                    "Mystery escape room",
                    "Stargazing night",
                    "Street art hunt",
                    "Wildlife safari",
                    "Antique market hunt",
                    "Aurora hunting",
                    "Poetry slam night",
                    "Glacier hiking",
                    "Live jazz show",
                    "Geocaching adventure",
                    "Botanical garden visit",
                    "Kayaking trip",
                    "Stand-up comedy",
                    "Salsa dancing",
                    "Wilderness camping",
                    "Sculpture park visit",
                    "Roller coaster thrill",
                    "Potluck picnic",
                    "Vintage car ride",
                    "Historical reenactment",
                    "Outdoor cinema",
                    "Karaoke contest",
                    "Street photography",
                    "Mindfulness retreat",
                    "Jet skiing",
                    "Local brewery tour",
                    "Bookstore crawl"
                )

                NameSuggestionEntity.new(DEFAULT_TASK_NAME_SUGGESTIONS_ID) {
                    description = "Default task names"
                    names = defaultNames
                }
            }
        }
    }
     */
}
