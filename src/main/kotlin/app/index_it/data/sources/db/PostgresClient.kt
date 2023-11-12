package app.index_it.data.sources.db

import app.index_it.Env
import app.index_it.core.logic.typedId.toIxIntId
import app.index_it.data.sources.db.dbi.suggestion.impl.SuggestionColorsDBIImpl
import app.index_it.data.sources.db.dbi.suggestion.impl.SuggestionNamesDBIImpl
import app.index_it.data.sources.db.schemas.lists.CategoryTable
import app.index_it.data.sources.db.schemas.lists.ItemContentTable
import app.index_it.data.sources.db.schemas.lists.ItemTable
import app.index_it.data.sources.db.schemas.lists.ListTable
import app.index_it.data.sources.db.schemas.suggestions.*
import app.index_it.data.sources.db.schemas.tasks.SubTaskTable
import app.index_it.data.sources.db.schemas.tasks.TaskTable
import app.index_it.data.sources.db.schemas.user.EmailVerificationTable
import app.index_it.data.sources.db.schemas.user.PasswordResetTable
import app.index_it.data.sources.db.schemas.user.UserTable
import app.index_it.data.sources.db.schemas.web.NotifyTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object PostgresClient {
    private val database = Database.connect(
        url = Env.postgres_url,
        driver = "org.postgresql.Driver",
        user = Env.postgres_user,
        password = Env.postgres_password
    )

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(
            context = Dispatchers.IO,
        ) { block() }

    private const val DEFAULT_COLOR_SUGGESTIONS_ID = 1

    private const val DEFAULT_LIST_NAME_SUGGESTIONS_ID = 1
    private const val DEFAULT_CATEGORY_NAME_SUGGESTIONS_ID = 2
    private const val DEFAULT_ITEM_NAME_SUGGESTIONS_ID = 3
    private const val DEFAULT_TASK_NAME_SUGGESTIONS_ID = 4

    suspend fun init() {
        createTables()
        setupColorSuggestions()
        setupListNameSuggestions()
        setupCategoryNameSuggestions()
        setupItemNameSuggestions()
        setupItemNameSuggestions()
    }

    private suspend fun createTables() {
        dbQuery {
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

                TaskTable,
                SubTaskTable
            )
        }
    }

    private suspend fun setupColorSuggestions() {
        dbQuery {
            if (SuggestionColorsDBIImpl.get(DEFAULT_COLOR_SUGGESTIONS_ID.toIxIntId()) == null) {
                ColorSuggestionEntity.new(DEFAULT_COLOR_SUGGESTIONS_ID) {
                    description = "Default colors"
                }

                ColorTable.deleteWhere { ColorTable.suggestion eq DEFAULT_COLOR_SUGGESTIONS_ID }

                val defaultColors = listOf(
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

                ColorTable.batchInsert(defaultColors) {
                    this[ColorTable.suggestion] = DEFAULT_COLOR_SUGGESTIONS_ID
                    this[ColorTable.color] = it
                }
            }
        }
    }

    private suspend fun setupListNameSuggestions() {
        if (SuggestionNamesDBIImpl.get(DEFAULT_LIST_NAME_SUGGESTIONS_ID.toIxIntId()) == null) {
            NameSuggestionEntity.new(DEFAULT_LIST_NAME_SUGGESTIONS_ID) {
                description = "Default list names"
            }

            NameTable.deleteWhere { NameTable.suggestion eq DEFAULT_LIST_NAME_SUGGESTIONS_ID }

            val defaultNames = listOf(
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

            NameTable.batchInsert(defaultNames) {
                this[NameTable.suggestion] = DEFAULT_LIST_NAME_SUGGESTIONS_ID
                this[NameTable.name] = it
            }
        }
    }

    private suspend fun setupCategoryNameSuggestions() {
        if (SuggestionNamesDBIImpl.get(DEFAULT_ITEM_NAME_SUGGESTIONS_ID.toIxIntId()) == null) {
            NameSuggestionEntity.new(DEFAULT_ITEM_NAME_SUGGESTIONS_ID) {
                description = "Default category names"
            }

            NameTable.deleteWhere { NameTable.suggestion eq DEFAULT_ITEM_NAME_SUGGESTIONS_ID }

            val defaultNames = listOf(
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

            NameTable.batchInsert(defaultNames) {
                this[NameTable.suggestion] = DEFAULT_ITEM_NAME_SUGGESTIONS_ID
                this[NameTable.name] = it
            }
        }
    }

    private suspend fun setupItemNameSuggestions() {
        if (SuggestionNamesDBIImpl.get(DEFAULT_CATEGORY_NAME_SUGGESTIONS_ID.toIxIntId()) == null) {
            NameSuggestionEntity.new(DEFAULT_CATEGORY_NAME_SUGGESTIONS_ID) {
                description = "Default item names"
            }

            NameTable.deleteWhere { NameTable.suggestion eq DEFAULT_CATEGORY_NAME_SUGGESTIONS_ID }

            val defaultNames = listOf(
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

            NameTable.batchInsert(defaultNames) {
                this[NameTable.suggestion] = DEFAULT_CATEGORY_NAME_SUGGESTIONS_ID
                this[NameTable.name] = it
            }
        }
    }

    private suspend fun setupTaskNameSuggestions() {
        if (SuggestionNamesDBIImpl.get(DEFAULT_TASK_NAME_SUGGESTIONS_ID.toIxIntId()) == null) {
            NameSuggestionEntity.new(DEFAULT_TASK_NAME_SUGGESTIONS_ID) {
                description = "Default task names"
            }

            NameTable.deleteWhere { NameTable.suggestion eq DEFAULT_TASK_NAME_SUGGESTIONS_ID }

            val defaultNames = listOf(
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
                "Rollercoaster thrill",
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

            NameTable.batchInsert(defaultNames) {
                this[NameTable.suggestion] = DEFAULT_TASK_NAME_SUGGESTIONS_ID
                this[NameTable.name] = it
            }
        }
    }

    fun getDb() = database
}