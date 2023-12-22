package app.index.core.logic.usecases

import app.index.core.logic.DatetimeUtils
import app.index.data.models.tasks.TaskData
import app.index.data.models.tasks.TaskReminderData
import org.dmfs.rfc5545.recur.RecurrenceRule
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import kotlin.math.max

object TaskUseCase {

    /**
     * If a task is recurring, this calculates the next occurrence date and the updated rrule in case `COUNT` was used as the end clause
     *
     * @return Null if this task isn't recurring or if it reached the end clause, a [Pair] with the next occurrence timestamp and updated rrule otherwise
     */
    fun calculateNextOccurrenceDueDateAndRRule(task: TaskData): Pair<Long, String>? {
        if (task.dueDate == null || task.rrule == null) {
            return null
        }

        val rrule = RecurrenceRule(task.rrule)

        if (rrule.count != null) {
            rrule.count -= 1

            if (rrule.count < 1) {
                return null
            }
        }

        return rrule
            .iterator(max(task.dueDate, DatetimeUtils.currentMillis()), DatetimeUtils.utcTimeZone)
            .apply {
                try {
                    skip(1)
                } catch (_: Exception) {
                }
            }
            .takeIf { it.hasNext() }
            ?.nextMillis()
            ?.let {
                Pair(it, rrule.toString())
            }
    }

    /**
     * Calculates the timestamp for the on day reminder
     *
     * @return Timestamp for the on day reminder, null for no reminder
     */
    fun calculateOnDayReminderTimestamp(
        dueDate: Long?,
        onDayReminder: Long?,
    ): Long? {
        if (dueDate == null || onDayReminder == null) {
            return null
        }

        return (dueDate + onDayReminder).takeIf { it > DatetimeUtils.currentMillis() }
    }

    /**
     * Calculates all the timestamps for the reminders of the task
     *
     * @param dueDate
     * @param reminders make sure those are validated as this function does not validate them
     *
     * @return the list of timestamps
     */
    fun calculateReminderTimestamps(dueDate: Long?, reminders: List<TaskReminderData>): List<Long> {
        if (dueDate == null) {
            return emptyList()
        }

        val timestamps = mutableListOf<Long>()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC))

        reminders.forEach { taskReminder ->
            try {
                calendar.time = Date(dueDate)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                calendar.roll(taskReminder.daysBefore, false)

                timestamps.add(calendar.timeInMillis.plus(taskReminder.timeOffset))
            } catch (_: Exception) {
                // invalid date info
            }
        }

        return timestamps
    }
}
