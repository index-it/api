package app.index.data.daos.task

import app.index.core.logic.DatetimeUtils
import app.index.core.logic.typedId.newIxId
import app.index.core.logic.usecases.TaskUseCase
import app.index.data.models.tasks.TaskDto
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
            TaskDto(
                id = newIxId(),
                userId = newIxId(),
                itemId = null,
                name = "test",
                description = null,
                subTasks = emptyList(),
                dueDate = currentMillis,
                rrule = rrule,
            )

        val (nextDueDate, _) =
            TaskUseCase.calculateNextOccurrenceDueDateAndRRule(task)
                ?: fail("Didn't recognize task next occurrence with rrule `$rrule`!")

        assertEquals((expectedNewDueDate) / 10000, nextDueDate / 10000)

        // Second occurrence
        expectedNewDueDate += DatetimeUtils.ONE_DAY_MILLIS

        val newOccurrence =
            TaskDto(
                id = newIxId(),
                userId = task.userId,
                itemId = task.itemId,
                name = task.name,
                description = task.description,
                dueDate = nextDueDate,
                rrule = rrule,
                subTasks = task.subTasks.map { it.apply { completed = false } },
                completed = false,
                createdAt = DatetimeUtils.currentMillis(),
                editedAt = task.editedAt,
                completedAt = null,
            )

        val thirdOccurrence =
            TaskUseCase.calculateNextOccurrenceDueDateAndRRule(newOccurrence)
                ?: fail("Didn't recognize task next occurrence with rrule `$rrule`!")

        assertEquals((expectedNewDueDate) / 10000, thirdOccurrence.first / 10000)
    }
}
