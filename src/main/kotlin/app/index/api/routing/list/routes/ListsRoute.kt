package app.index.api.routing.list.routes

import app.index.api.plugins.emitAnalyticsEvent
import app.index.api.plugins.emitWebsocketEventForCurrentSessionUser
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.AnalyticsEventManager
import app.index.core.logic.websocket.WebsocketEventManager
import app.index.core.logic.websocket.event.WebsocketEventContent
import app.index.core.logic.websocket.event.WebsocketEventType
import app.index.data.daos.list.ListDao
import app.index.data.models.analytics.AnalyticsEventData
import app.index.data.models.lists.ListData
import app.index.data.validation.Validations
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.listsRoute() {
    val listDao by inject<ListDao>()
    val websocketEventManager by inject<WebsocketEventManager>()
    val analyticsEventManager by inject<AnalyticsEventManager>()

    get<ListsRoute>({
        tags = listOf("lists")
        operationId = "get-lists"
        summary = "gets all the lists the user has access to"
        response {
            HttpStatusCode.OK to {
                description = "user lists"
                body<List<ListData>>()
            }
        }
    }) {
        call.respond(listDao.getListsAccessibleByUser(userIdFromSessionOrThrow()))
    }

    post<ListsRoute>({
        tags = listOf("lists")
        operationId = "create-list"
        summary = "create a new list"
        request {
            body<ListData.ListCreateRequestData> {
                required = true
                example("sample-list", ListData.ListCreateRequestData("places", "🏝️", "#343322"))
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "list created"
                body<ListData> {
                    description = "the created list"
                }
            }
            HttpStatusCode.BadRequest to {
                description = "invalid parameters\n${Validations.List.VALIDATIONS_SUMMARY}"
            }
        }
    }) {
        val userId = userIdFromSessionOrThrow()
        val newList = call.receive<ListData.ListCreateRequestData>()

        val created = listDao.create(userId, newList)

        call.respond(created)

        emitWebsocketEventForCurrentSessionUser(
            websocketEventManager = websocketEventManager,
            type = WebsocketEventType.LIST_CREATED,
            content = WebsocketEventContent.ListCreateOrUpdateEventContent(created)
        )

        emitAnalyticsEvent(
            analyticsEventManager = analyticsEventManager,
            analyticsEventData = AnalyticsEventData.ListCreationEventData(
                user_id = userId
            )
        )
    }
}
