package app.index.api.core.logic.usecases

import app.index.api.core.clients.BrevoClient
import app.index.api.core.logic.DatetimeUtils
import app.index.api.core.logic.TokenGenerator
import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.daos.list.ListDao
import app.index.api.data.daos.list.ListUserInviteDao
import app.index.api.data.models.lists.ListData
import app.index.api.data.models.lists.ListUserInviteData
import app.index.api.data.models.user.UserData
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ListInvitationUseCase : KoinComponent {
    private val listDao by inject<ListDao>()
    private val listUserInviteDao by inject<ListUserInviteDao>()
    private val tokenGenerator by inject<TokenGenerator>()
    private val brevoClient by inject<BrevoClient>()

    /**
     * Sends a list invitation to the provided user email
     *
     * @param fromUserEmail the email of the user who is sending the invitation
     * @param listName the name of the list
     * @param listId the id of the list
     * @param toUserEmail the email of the user to send the invitation to
     * @param editor whether the user should be an editor or just a viewer of the list
     *
     * @returns true if the email was sent successfully, false otherwise
     */
    suspend fun sendInvitation(
        fromUserEmail: String,
        toUserEmail: String,
        listId: IxId<ListData>,
        listName: String,
        editor: Boolean,
    ): Boolean {
        val (token, hashedToken) = tokenGenerator.generate()

        val listUserInviteData = ListUserInviteData(
            token = hashedToken,
            email = toUserEmail,
            listId = listId,
            editor = editor,
            expireAt = DatetimeUtils.currentMillis() + (DatetimeUtils.ONE_DAY_MILLIS * 7),
        )

        val sent = brevoClient.sendListInvitationEmail(
            inviterEmail = fromUserEmail,
            listName = listName,
            emailTo = toUserEmail,
            editor = editor,
            token = token,
        )

        if (sent) {
            listUserInviteDao.create(listUserInviteData)
        }

        return sent
    }

    /**
     * Adds permission to a user to access a list
     *
     * @param list the list to add the permission to
     * @param editor whether the user should be an editor or just a viewer of the list
     * @param userId the id of the user to add the permission to
     *
     * @returns a nullable pair with a boolean, true if the permission was added, false if it was present already, and the updated list. If the pair is null the list was not found
     */
    suspend fun addPermissionToUser(list: ListData, editor: Boolean, userId: IxId<UserData>): Pair<Boolean, ListData>? {
        val addAsViewer = !editor && list.viewers.none { user -> user == userId }
        val addAsEditor = editor && list.editors.none { user -> user == userId }

        if (addAsViewer || addAsEditor) {
            val updatedList = if (editor) {
                listDao.addPermissionToUser(list.id, userId, true)
            } else {
                listDao.addPermissionToUser(list.id, userId, false)
            } ?: return null

            return Pair(true, updatedList)
        } else {
            return Pair(false, list)
        }
    }
}