package app.index.core.logic.usecases

import app.index.core.clients.GoogleCloudSchedulerClient
import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.impl.IxId
import app.index.core.logic.typedId.newIxId
import app.index.data.daos.task.TaskDao
import app.index.data.daos.task.TaskReminderJobDao
import app.index.data.models.tasks.TaskData
import app.index.data.models.tasks.TaskReminderData
import app.index.data.models.tasks.TaskReminderJobData
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.ZoneOffset
import java.util.*
import kotlin.math.max

object TaskUseCase : KoinComponent {
    private val taskDao by inject<TaskDao>()
    private val taskReminderJobDao by inject<TaskReminderJobDao>()
    private val googleCloudSchedulerClient by inject<GoogleCloudSchedulerClient>()

    /**
     * If a task is recurring, this calculates the next occurrence date and the updated rrule in case `COUNT` was used as the end clause
     *
     * @return Null if this task isn't recurring or if it reached the end clause, a [Pair] with the next occurrence timestamp and updated rrule otherwise
     */
    fun calculateNextOccurrenceDueDateAndRRule(task: TaskData): Pair<Long, String>? {
        if (task.due_date == null || task.rrule == null) {
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
            .iterator(max(task.due_date, DatetimeUtils.currentMillis()), DatetimeUtils.utcTimeZone)
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
     * Creates the next occurrence of a recurring task if needed
     *
     * @param task
     *
     * @return the created [TaskData] or null if no next occurrence is needed
     *
     * @see calculateNextOccurrenceDueDateAndRRule
     */
    suspend fun createNextOccurrence(task: TaskData): TaskData? {
        val (dueDate, rrule) = calculateNextOccurrenceDueDateAndRRule(task) ?: return null

        val nextOccurrenceTask = taskDao.createNextOccurrence(task, dueDate, rrule)

        createReminders(nextOccurrenceTask)

        return nextOccurrenceTask
    }

    /**
     * Calculates all the timestamps for the reminders of the task
     *
     * @param dueDate
     * @param reminders make sure those are validated as this function does not validate them
     *
     * @return the list of timestamps
     */
    private fun calculateReminderTimestamps(dueDate: Long?, reminders: List<TaskReminderData>): List<Long> {
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

                calendar.roll(taskReminder.days_before, false)

                timestamps.add(calendar.timeInMillis.plus(taskReminder.time_offset))
            } catch (_: Exception) {
                // invalid date info
            }
        }

        return timestamps
    }

    /**
     * Creates all task reminder jobs for [TaskData.reminders]
     *
     * This method should be used for newly created tasks, if you have an existing tasks
     * and you need to recreate its reminders (for example when they get updated) see [refreshReminders]
     *
     * @param task
     *
     * @see refreshReminders
     */
    suspend fun createReminders(task: TaskData) {
        val currentMillis = DatetimeUtils.currentMillis()
        val reminderJobs = calculateReminderTimestamps(task.due_date, task.reminders)
            .filter { it > currentMillis }
            .map {
                TaskReminderJobData.TaskReminderJobCreateData(
                    id = newIxId(),
                    taskId = task.id,
                    userId = task.user_id,
                    scheduledAt = it
                )
            }

        if (reminderJobs.isNotEmpty()) {
            taskReminderJobDao.createAll(reminderJobs)
            reminderJobs.forEach { reminderJob ->
                googleCloudSchedulerClient.createTaskReminderJob(reminderJob.id, reminderJob.scheduledAt)
            }
        }
    }

    /**
     * Finds and delete outdated task reminder jobs + detects and creates missing task reminder jobs
     *
     * @param task
     */
    suspend fun refreshReminders(task: TaskData) {
        val reminderTimestamps = calculateReminderTimestamps(task.due_date, task.reminders)
        val existingReminderJobs = taskReminderJobDao.getAllOfTask(task.id)

        // Find and delete outdated jobs
        val outdatedReminderJobs = mutableListOf<IxId<TaskReminderJobData>>()

        existingReminderJobs.forEach { reminderJobData ->
            if (reminderJobData.scheduledAt !in reminderTimestamps) {
                outdatedReminderJobs.add(reminderJobData.id)
            }
        }

        if (outdatedReminderJobs.isNotEmpty()) {
            taskReminderJobDao.deleteMultiple(outdatedReminderJobs)
            outdatedReminderJobs.forEach { jobId ->
                googleCloudSchedulerClient.deleteTaskReminderJob(jobId)
            }
        }

        // Detect and create missing jobs
        val missingReminderJobs = mutableListOf<TaskReminderJobData.TaskReminderJobCreateData>()
        val currentMillis = DatetimeUtils.currentMillis()

        reminderTimestamps
            .filter { it > currentMillis }
            .forEach { timestamp ->
                if (existingReminderJobs.none { reminderJob -> reminderJob.scheduledAt == timestamp }) {
                    missingReminderJobs.add(
                        TaskReminderJobData.TaskReminderJobCreateData(
                            id = newIxId(),
                            taskId = task.id,
                            userId = task.user_id,
                            scheduledAt = timestamp
                        )
                    )
                }
            }

        if (missingReminderJobs.isNotEmpty()) {
            taskReminderJobDao.createAll(missingReminderJobs)
            missingReminderJobs.forEach { reminderJob ->
                googleCloudSchedulerClient.createTaskReminderJob(reminderJob.id, reminderJob.scheduledAt)
            }
        }
    }
}
