package app.index.api.routing.user.routes

import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.user.MeRoute
import app.index.core.logic.DatetimeUtils
import app.index.data.daos.user.FCMRegistrationTokenDao
import app.index.data.models.user.FCMRegistrationTokenData
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.fcmRoutes() {
    val fcmRegistrationTokenDao by inject<FCMRegistrationTokenDao>()

    post<MeRoute.NotificationsRoute.RegistrationRoute>({
        tags = listOf("user")
        operationId = "notification-token"
        summary = "saves a user device notification token"
        request {
            body<FCMRegistrationTokenData.FCMRegistrationTokenRequestBody> {
                description = "the new registration token"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "registration token saved"
            }
        }
    }) {
        val fcmRegistrationTokenData = FCMRegistrationTokenData(
            token = call.receive<FCMRegistrationTokenData.FCMRegistrationTokenRequestBody>().token,
            userId = userIdFromSessionOrThrow(),
            createdAt = DatetimeUtils.currentMillis(),
        )

        fcmRegistrationTokenDao.upsert(fcmRegistrationTokenData)

        call.respond(HttpStatusCode.OK)
    }
}
