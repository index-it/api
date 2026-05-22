package app.index.api.routing.user.routes

import app.index.shared.core.logic.DatetimeUtils
import app.index.api.data.daos.user.FCMRegistrationTokenDao
import app.index.shared.core.data.models.user.FCMRegistrationTokenData
import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.user.MeRoute
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.fcmRoutes() {
    val fcmRegistrationTokenDao by inject<FCMRegistrationTokenDao>()

    /**
     * Saves a user device notification token.
     *
     * Tag: user
     *
     * Security: session
     */
    post<MeRoute.NotificationsRoute.RegistrationRoute> {
        val fcmRegistrationTokenData = FCMRegistrationTokenData(
            token = call.receive<FCMRegistrationTokenData.FCMRegistrationTokenRequestBody>().token,
            userId = userIdFromSessionOrThrow(),
            createdAt = DatetimeUtils.currentMillis(),
        )

        fcmRegistrationTokenDao.upsert(fcmRegistrationTokenData)

        call.respond(HttpStatusCode.OK)
    }
}
