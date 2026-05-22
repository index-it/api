package app.index.api.routing.task.routes

import app.index.api.core.exceptions.AuthorizationException
import app.index.api.core.logic.AnalyticsEventManager
import app.index.api.core.logic.usecases.ListAuthorizationUseCase
import app.index.api.core.logic.usecases.TaskUseCase
import app.index.api.core.logic.websocket.WebsocketEventManager
import app.index.api.core.logic.websocket.event.WebsocketEventContent
import app.index.api.core.logic.websocket.event.WebsocketEventType
import app.index.api.data.daos.list.ItemDao
import app.index.api.data.daos.task.TaskDao
import app.index.api.data.daos.task.TaskReminderJobDao
import app.index.shared.core.data.models.analytics.AnalyticsEventData
import app.index.shared.core.data.models.lists.ListAuthorizationLevel
import app.index.api.plugins.emitAnalyticsEvent
import app.index.api.plugins.emitWebsocketEventForCurrentSessionUser
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.task.TasksRoute
import io.ktor.http.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.taskCompletionRoute() {
    val taskDao by inject<TaskDao>()
    val taskReminderJobDao by inject<TaskReminderJobDao>()
    val itemDao by inject<ItemDao>()
    val websocketEventManager by inject<WebsocketEventManager>()
    val analyticsEventManager by inject<AnalyticsEventManager>()

    /**
     * Completes or un-completes a task and a related item if existing.
     *
     * Tag: tasks
     *
     * Security: session
     */
    put<TasksRoute.TaskRoute.CompletionRoute> {
        val userId = userIdFromSessionOrThrow()

        val updatedTask = taskDao.setCompletion(userId, it.parent.task_id, it.completed)
            ?: return@put call.respond(HttpStatusCode.NotFound)

        if (it.completed) {
            taskReminderJobDao.deleteAllOfTask(updatedTask.id)

            TaskUseCase.createNextOccurrence(updatedTask)
                ?.also { nextOccurrenceTask ->
                    emitWebsocketEventForCurrentSessionUser(
                        websocketEventManager = websocketEventManager,
                        type = WebsocketEventType.TASK_CREATED,
                        content = WebsocketEventContent.TaskCreateOrUpdateEventContent(nextOccurrenceTask),
                        includeCurrentSession = true
                    )
                }
        } else if (updatedTask.rrule != null) {
            return@put call.respond(HttpStatusCode.MethodNotAllowed, "Cannot un-complete task with recurring rule")
        }

        call.respond(updatedTask)

        emitWebsocketEventForCurrentSessionUser(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.TASK_UPDATED,
            content = WebsocketEventContent.TaskCreateOrUpdateEventContent(updatedTask)
        )

        emitAnalyticsEvent(
            analyticsEventManager = analyticsEventManager,
            analyticsEventData = AnalyticsEventData.TaskCompletionEventData(
                user_id = userId,
                task_id = updatedTask.id,
                completed = it.completed
            )
        )

        // update completion of connected item if user has at least editor permissions in its list
        if (updatedTask.item_id != null) {
            val item = itemDao.get(updatedTask.item_id)

            if (item !== null) {
                try {
                    ListAuthorizationUseCase.getListIfAuthorized(
                        listId = item.list_id,
                        userId = userIdFromSessionOrThrow(),
                        authorizationLevel = ListAuthorizationLevel.EDITOR
                    )

                    itemDao.setCompletion(updatedTask.item_id, it.completed)
                        ?.also { updatedConnectedItem ->
                            emitWebsocketEventForCurrentSessionUser(
                                websocketEventManager = websocketEventManager,
                                type = WebsocketEventType.ITEM_UPDATED,
                                content = WebsocketEventContent.ItemCreateOrUpdateEventContent(updatedConnectedItem),
                                includeCurrentSession = true
                            )
                        }
                } catch (_: AuthorizationException) {
                    // Do not update the item completion if the user is not at least an editor of the list
                }
            }
        }
    }
}
