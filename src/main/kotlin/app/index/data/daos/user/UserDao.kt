package app.index.data.daos.user

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserData
import app.index.data.sources.cache.cm.lists.CategoryCM
import app.index.data.sources.cache.cm.lists.ItemCM
import app.index.data.sources.cache.cm.lists.ItemContentCM
import app.index.data.sources.cache.cm.lists.ListCM
import app.index.data.sources.cache.cm.tasks.TaskCM
import app.index.data.sources.cache.cm.users.UserCM
import app.index.data.sources.db.dbi.user.UserDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class UserDao(
    private val userDBI: UserDBI,
    private val userCM: UserCM,
    private val listCM: ListCM,
    private val categoryCM: CategoryCM,
    private val itemCM: ItemCM,
    private val itemContentCM: ItemContentCM,
    private val taskCM: TaskCM,
) {
    suspend fun create(userData: UserData) {
        userDBI.create(userData)
        userCM.cache(userData)
    }

    suspend fun get(id: IxId<UserData>): UserData? {
        var user = userCM.get(id)

        if (user == null) {
            user = userDBI.get(id) ?: return null
            userCM.cache(user)
        }

        return user
    }

    /**
     * This method should be only used in the login route
     */
    suspend fun getFromEmail(email: String): UserData? {
        return userDBI.get(email)
    }

    suspend fun verifyEmail(id: IxId<UserData>) {
        userDBI.verifyEmail(id)
        userCM.delete(id)
    }

    suspend fun resetPassword(
        id: IxId<UserData>,
        newPasswordHashed: String,
        verifyEmail: Boolean,
    ) {
        userDBI.resetPassword(id, newPasswordHashed, verifyEmail)
        userCM.delete(id)
    }

    suspend fun delete(id: IxId<UserData>) {
        userCM.delete(id)
        userDBI.delete(id)

        // Invalidate all user cache, the database handles that itself instead with cascade operation
        listCM.deleteAllOfUser(id)
        categoryCM.deleteAllOfUser(id)
        itemCM.deleteAllOfUser(id)
        itemContentCM.deleteAllOfUser(id)
        taskCM.deleteAllOfUser(id)
    }
}
