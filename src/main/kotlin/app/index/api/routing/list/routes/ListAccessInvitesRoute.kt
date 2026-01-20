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
     * retrieve all existing invites for a list
     *
     * @tag lists-access
     * @operationId get-list-access-invites
     * @path list_id the id of the list
     * @response 200 list of active invites
     * @response 401 user not authenticated
     * @response 403 missing required list permission: owner
     * @response 404 list not found
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
     * create a new invite for a list
     *
     * @tag lists-access
     * @operationId create-list-access-invite
     * @path list_id the id of the list
     * @requestBody application/json invite parameters
     * @response 200 invite created
     * @response 401 user not authenticated
     * @response 403 missing required list permission: owner
     * @response 404 list not found
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
            maxUsages = inviteData.maxUsages,
            description = inviteData.description,
            expiresAt = inviteData.expiresAt
        )

        listInviteDao.create(listInviteData)

        call.respond(listInviteData.copy(token=token))
    }

    /**
     * delete an existing invite for a list
     *
     * @tag lists-access
     * @operationId delete-list-access-invite
     * @path list_id the id of the list
     * @path invite_id the id of the invite
     * @response 200 invite deleted
     * @response 401 user not authenticated
     * @response 403 missing required list permission: owner
     * @response 404 list or invite not found
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