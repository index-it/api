package app.index.data.daos.task

import app.index.shared.core.logic.DatetimeUtils
import app.index.shared.core.typedId.newIxId
import app.index.shared.core.logic.usecases.TaskUseCase
import app.index.shared.core.data.models.tasks.TaskData
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.junit5.JUnit5Asserter.fail

class TaskDaoTest {
    @Test
    fun calculateNextOccurrenceDueDateAndRRule() {
        val rrule = "FREQ=DAILY;"
        val currentLocalDate = DatetimeUtils.currentLocalDate()
        var expectedNewLocalDate = currentLocalDate.plus(1, DateTimeUnit.DAY)

        // First occurrence
        val task =
            TaskData(
                id = newIxId(),
                user_id = newIxId(),
                item_id = null,
                name = "test",
                description = null,
                subtasks = emptyList(),
                due_date = currentLocalDate,
                rrule = rrule,
            )

        val (nextDueDate, _) =
            TaskUseCase.calculateNextOccurrenceDueDateAndRRule(task)
                ?: fail("Didn't recognize task next occurrence with rrule `$rrule`!")

        assertEquals(expectedNewLocalDate, nextDueDate)

        // Second occurrence
        expectedNewLocalDate = expectedNewLocalDate.plus(1, DateTimeUnit.DAY)

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

        val (nextNextDueDate, _) =
            TaskUseCase.calculateNextOccurrenceDueDateAndRRule(newOccurrence)
                ?: fail("Didn't recognize task next occurrence with rrule `$rrule`!")

        assertEquals(expectedNewLocalDate, nextNextDueDate)
    }
}
