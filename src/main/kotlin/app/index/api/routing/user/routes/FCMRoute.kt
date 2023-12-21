package app.index.api.routing.user.routes

import app.index.api.plugins.userIdFromSession
import app.index.api.routing.user.MeRoute
import app.index.core.logic.DatetimeUtils
import app.index.data.daos.user.FCMRegistrationTokenDao
import app.index.data.models.user.FCMRegistrationTokenDto
import io.github.smiley4.ktorswaggerui.dsl.resources.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.fcmRoutes() {
    val fcmRegistrationTokenDao by inject<FCMRegistrationTokenDao>()

    post<MeRoute.Notifications.Token>({
        tags = listOf("user")
        operationId = "notification-token"
        summary = "saves a user device notification token"
        request {
            body<FCMRegistrationTokenDto.FCMRegistrationTokenRequestBody> {
                description = "the new registration token"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "registration token saved"
            }
        }
    }) {
        val fcmRegistrationTokenDto =
            FCMRegistrationTokenDto(
                token = call.receive<FCMRegistrationTokenDto.FCMRegistrationTokenRequestBody>().token,
                userId = userIdFromSession()!!,
                createdAt = DatetimeUtils.currentMillis(),
            )

        fcmRegistrationTokenDao.createOrUpdate(fcmRegistrationTokenDto)

        call.respond(HttpStatusCode.OK)
    }
}
