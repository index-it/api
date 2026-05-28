package app.index.shared.core.logic.pro

import app.index.shared.core.clients.RevenueCatClient
import app.index.shared.core.typedId.toIxId
import app.index.shared.core.data.daos.user.UserDao
import app.index.shared.core.data.models.user.UserData
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ProManager(
    private val userDao: UserDao,
    private val revenueCatClient: RevenueCatClient
) {

    /**
     * Fetches all the users with the given [userIds] and tries to find an entitlement.
     *
     * If an entitlement is found, the user's pro status is updated.
     *
     * @return the new [UserData] if updated, null if everything was already synced
     */
   suspend fun refreshProStatus(
        userIds: List<String>
    ): UserData? {
        userIds.forEach { id ->
            val userId = try {
                id.toIxId<UserData>()
            } catch (_: IllegalArgumentException) {
                null
            }

            if (userId != null) {
                val userData = userDao.get(userId)

                if (userData != null) {
                    val hasPro = revenueCatClient.isUserPro(userId.toString())

                    if (userData.has_pro != hasPro) {
                        val newUserData = userDao.setHasPro(userId, hasPro)

                        if (newUserData != null) {
                            return newUserData
                        }
                    }
                }
            }
        }

        return null
    }
}