package app.index.api.core.logic.usecases

import app.index.api.core.exceptions.AuthorizationException
import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.daos.list.ListDao
import app.index.shared.core.data.models.lists.ListAuthorizationLevel
import app.index.shared.core.data.models.lists.ListData
import app.index.shared.core.data.models.user.UserData
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ListAuthorizationUseCase : KoinComponent {
    private val listDao by inject<ListDao>()

    /**
     * Returns the list if the user is authorized based on the [authorizationLevel]
     *
     * The [authorizationLevel] is inclusive, meaning:
     * - owner includes both editor and viewer
     * - editor includes viewer
     *
     * @param listId
     * @param userId
     * @param authorizationLevel
     *
     * @throws
     * @return the [ListData] if authorized, or null if the list with the provided [listId] doesn't exist
     */
    suspend fun getListIfAuthorized(
        listId: IxId<ListData>,
        userId: IxId<UserData>,
        authorizationLevel: ListAuthorizationLevel
    ): ListData? {
        val list = listDao.get(listId)
            ?: return null

        when (authorizationLevel) {
            ListAuthorizationLevel.OWNER -> {
                if (list.user_id != userId)
                    throw AuthorizationException("You need to be the owner of the list to perform this action")
            }
            ListAuthorizationLevel.EDITOR -> {
                if (list.user_id != userId && list.editors.none { it == userId })
                    throw AuthorizationException("You need to be the owner or an editor of the list to perform this action")
            }
            ListAuthorizationLevel.VIEWER -> {
                if (list.user_id != userId && list.editors.none { it == userId } && list.viewers.none { it == userId } && !list.public)
                    throw AuthorizationException("The list needs to be public or you need to be the owner, an editor or a viewer of the list to perform this action")
            }
        }

        return list
    }
}