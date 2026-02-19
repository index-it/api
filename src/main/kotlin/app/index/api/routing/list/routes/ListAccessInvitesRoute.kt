package app.index.api.routing.list.routes

import app.index.api.plugins.userIdFromSessionOrThrow
import app.index.api.routing.list.ListsRoute
import app.index.core.logic.TokenGenerator
import app.index.core.logic.typedId.newIxId
import app.index.core.logic.usecases.ListAuthorizationUseCase
import app.index.data.daos.list.ListInviteDao
import app.index.data.models.lists.ListAuthorizationLevel
import app.index.data.models.lists.ListInviteData
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.resources.post
import org.koin.ktor.ext.inject

private val logger = KotlinLogging.logger {  }

fun Route.listAccessInvitesRoute() {
    val listInviteDao by inject<ListInviteDao>()
    val tokenGenerator by inject<TokenGenerator>()

    /**
     * Retrieve all existing invites for a list.
     *
     * Tag: lists-access
     *
     * Security: session
     */
    get<ListsRoute.ListRoute.AccessRoute.InvitesRoute> {
        val listId = it.parent.parent.list_id
        ListAuthorizationUseCase.getListIfAuthorized(
            listId = listId,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.OWNER
        ) ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(listInviteDao.get(listId).map { invite -> invite.asResponseData() })
    }

    /**
     * Creates a new invite for a list.
     *
     * Tag: lists-access
     *
     * Security: session
     */
    post<ListsRoute.ListRoute.AccessRoute.InvitesRoute> {
        logger.info { "we here" }
        val listId = it.parent.parent.list_id
        val inviteData = call.receive<ListInviteData.ListInviteCreateRequestData>()

        logger.info { "inviteData: $inviteData" }

        ListAuthorizationUseCase.getListIfAuthorized(
            listId = listId,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.OWNER
        ) ?: return@post call.respond(HttpStatusCode.NotFound)

        logger.info { "we here 2" }

        val (token, hashedToken) = tokenGenerator.generate()
        val listInviteData = ListInviteData(
            id = newIxId(),
            token = hashedToken,
            listId = listId,
            editor = inviteData.editor,
            maxUsages = inviteData.max_usages,
            description = inviteData.description,
            expiresAt = inviteData.expires_at
        )

        listInviteDao.create(listInviteData)

        call.respond(listInviteData.copy(token=token))
    }

    /**
     * Deletes an existing invite for a list.
     *
     * Tag: lists-access
     *
     * Security: session
     */
    delete<ListsRoute.ListRoute.AccessRoute.InvitesRoute.InviteRoute> {
        val listId = it.parent.parent.parent.list_id
        val inviteId = it.invite_id

        ListAuthorizationUseCase.getListIfAuthorized(
            listId = listId,
            userId = userIdFromSessionOrThrow(),
            authorizationLevel = ListAuthorizationLevel.OWNER
        ) ?: return@delete call.respond(HttpStatusCode.NotFound)

        listInviteDao.delete(inviteId)

        call.respond(HttpStatusCode.OK)
    }
}