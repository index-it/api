package app.index.data.daos.task

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.newIxId
import app.index.core.logic.usecases.TaskUseCase
import app.index.data.models.tasks.TaskData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.junit5.JUnit5Asserter.fail

class TaskDaoTest {
    @Test
    fun calculateNextOccurrenceDueDateAndRRule() {
        val rrule = "FREQ=DAILY;"
        val currentMillis = DatetimeUtils.currentMillis()
        var expectedNewDueDate = currentMillis + DatetimeUtils.ONE_DAY_MILLIS

        // First occurrence
        val task =
            TaskData(
                id = newIxId(),
                user_id = newIxId(),
                item_id = null,
                name = "test",
                description = null,
                subtasks = emptyList(),
                due_date = currentMillis,
                rrule = rrule,
            )

        val (nextDueDate, _) =
            TaskUseCase.calculateNextOccurrenceDueDateAndRRule(task)
                ?: fail("Didn't recognize task next occurrence with rrule `$rrule`!")

        assertEquals((expectedNewDueDate) / 10000, nextDueDate / 10000)

        // Second occurrence
        expectedNewDueDate += DatetimeUtils.ONE_DAY_MILLIS

        val newOccurrence =
            TaskData(
                id = newIxId(),
                user_id = task.user_id,
                item_id = task.item_id,
                name = task.name,
                description = task.description,
                due_date = nextDueDate,
                rrule = rrule,
                subtasks = task.subtasks.map { it.apply { completed = false } },
                completed = false,
                created_at = DatetimeUtils.currentMillis(),
                edited_at = task.edited_at,
                completed_at = null,
            )

        val thirdOccurrence =
            TaskUseCase.calculateNextOccurrenceDueDateAndRRule(newOccurrence)
                ?: fail("Didn't recognize task next occurrence with rrule `$rrule`!")

        assertEquals((expectedNewDueDate) / 10000, thirdOccurrence.first / 10000)
    }
}
