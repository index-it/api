package app.index_it.core.logic.usecases

import app.index_it.core.logic.DatetimeUtils
import app.index_it.data.models.tasks.TaskDto
import org.dmfs.rfc5545.recur.RecurrenceRule
import kotlin.math.max

object TaskUseCase {

    /**
     * If a task is recurring, this calculates the next occurrence date and the updated rrule in case `COUNT` was used as the end clause
     *
     * @return Null if this task isn't recurring or if it reached the end clause, a [Pair] with the next occurrence timestamp and updated rrule otherwise
     */
    fun calculateNextOccurrenceDueDateAndRRule(task: TaskDto): Pair<Long, String>? {
        if (task.dueDate == null || task.rrule == null)
            return null

        val rrule = RecurrenceRule(task.rrule)

        if (rrule.count != null) {
            rrule.count -= 1

            if (rrule.count < 1)
                return null
        }

        return rrule
            .iterator(max(task.dueDate, DatetimeUtils.currentMillis()), DatetimeUtils.utcTimeZone)
            .apply {
                try { skip(1) } catch (_: Exception) {}
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
    fun calculateOnDayReminderTimestamp(dueDate: Long?, onDayReminder: Long?): Long? {
        if (dueDate == null || onDayReminder == null) {
            return null
        }

        return (dueDate + onDayReminder).takeIf { it > DatetimeUtils.currentMillis() }
    }
}