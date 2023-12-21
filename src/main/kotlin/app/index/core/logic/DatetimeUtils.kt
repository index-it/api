package app.index.core.logic

import java.time.ZoneOffset
import java.util.*

object DatetimeUtils {
    val utcTimeZone: TimeZone = TimeZone.getTimeZone(ZoneOffset.UTC)

    const val ONE_DAY_SECONDS: Long = 24 * 60 * 60
    const val ONE_DAY_MILLIS: Long = ONE_DAY_SECONDS * 1000

    /**
     * Shortcut for [System.currentTimeMillis]
     */
    fun currentMillis(): Long = System.currentTimeMillis()
}
