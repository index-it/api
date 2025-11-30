package app.index.core.logic

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.Instant
import java.time.ZoneOffset
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
object DatetimeUtils {
    val javaUtcTimeZone = java.util.TimeZone.getTimeZone(ZoneOffset.UTC)
    val utcTimeZone: TimeZone = TimeZone.UTC

    const val ONE_DAY_SECONDS: Long = 24 * 60 * 60
    const val ONE_DAY_MILLIS: Long = ONE_DAY_SECONDS * 1000

    /**
     * Shortcut for [System.currentTimeMillis]
     */
    fun currentMillis(): Long = System.currentTimeMillis()

    fun currentLocalDate(): LocalDate {
        return Clock.System.now()
            .toLocalDateTime(utcTimeZone)
            .date
    }

    fun currentLocalDateTime(): LocalDateTime = Clock.System.now().toLocalDateTime(utcTimeZone)

    /**
     * Useful for analytics event timestamps
     *
     * @return a correct utc timestamp string representation
     */
    fun currentLocalDateTimeString(): String = currentLocalDateTime().toString()

    fun currentInstant() = Clock.System.now()

    fun currentJavaInstant() = Instant.now()
}
