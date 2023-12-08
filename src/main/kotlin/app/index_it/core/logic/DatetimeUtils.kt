package app.index_it.core.logic

import java.time.ZoneOffset
import java.util.TimeZone

object DatetimeUtils {
    val utcTimeZone = TimeZone.getTimeZone(ZoneOffset.UTC)

    val oneDayMillis = 24 * 60 * 60 * 1000

    fun currentMillis() = System.currentTimeMillis()
}

