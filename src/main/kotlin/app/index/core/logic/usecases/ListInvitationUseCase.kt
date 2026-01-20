package app.index.core.logic.usecases

import app.index.core.clients.BrevoClient
import app.index.core.logic.DatetimeUtils
import app.index.core.logic.TokenGenerator
import app.index.core.logic.typedId.impl.IxId
import app.index.data.daos.list.ListUserInviteDao
import app.index.data.models.lists.ListData
import app.index.data.models.lists.ListUserInviteData
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ListInvitationUseCase : KoinComponent {
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
}