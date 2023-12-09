package app.index_it.data.daos.task

import app.index_it.core.logic.DatetimeUtils
import app.index_it.core.logic.typedId.newIxId
import app.index_it.core.logic.usecases.TaskUseCase
import app.index_it.data.models.tasks.TaskDto
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class TaskDaoTest {

    @Test
    fun calculateNextOccurrenceDueDateAndRRule() {
        val rrule = "FREQ=DAILY;"
        val currentMillis = DatetimeUtils.currentMillis()
        var expectedNewDueDate = currentMillis + DatetimeUtils.oneDayMillis

        // First occurrence
        val task = TaskDto(
            id = newIxId(),
            userId = newIxId(),
            itemId = null,
            name = "test",
            description = null,
            subTasks = emptyList(),
            dueDate = currentMillis,
            rrule = rrule
        )

        val (nextDueDate, _) = TaskUseCase.calculateNextOccurrenceDueDateAndRRule(task)
            ?: fail("Didn't recognize task next occurrence with rrule `$rrule`!")

        assertEquals((expectedNewDueDate) / 10000, nextDueDate / 10000)

        // Second occurrence
        expectedNewDueDate += DatetimeUtils.oneDayMillis

        val newOccurrence = TaskDto(
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

        val thirdOccurrence = TaskUseCase.calculateNextOccurrenceDueDateAndRRule(newOccurrence)
            ?: fail("Didn't recognize task next occurrence with rrule `$rrule`!")

        assertEquals((expectedNewDueDate) / 10000, thirdOccurrence.first / 10000)
    }
}