package app.index.core.logic

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.Instant
import java.time.ZoneOffset

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

    fun currentInstant() = Clock.System.now()

    fun currentJavaInstant() = Instant.now()
}
