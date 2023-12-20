package app.index_it.data.daos.user

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.lists.CategoryCM
import app.index_it.data.sources.cache.cm.lists.ItemCM
import app.index_it.data.sources.cache.cm.lists.ItemContentCM
import app.index_it.data.sources.cache.cm.lists.ListCM
import app.index_it.data.sources.cache.cm.lists.impl.CategoryCMImpl
import app.index_it.data.sources.cache.cm.lists.impl.ItemCMImpl
import app.index_it.data.sources.cache.cm.lists.impl.ItemContentCMImpl
import app.index_it.data.sources.cache.cm.lists.impl.ListCMImpl
import app.index_it.data.sources.cache.cm.tasks.TaskCM
import app.index_it.data.sources.cache.cm.tasks.impl.TaskCMImpl
import app.index_it.data.sources.cache.cm.users.UserCM
import app.index_it.data.sources.db.dbi.user.UserDBI
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class UserDao(
    private val userDBI: UserDBI,
    private val userCM: UserCM,
    private val listCM: ListCM,
    private val categoryCM: CategoryCM,
    private val itemCM: ItemCM,
    private val itemContentCM: ItemContentCM,
    private val taskCM: TaskCM
) {
    suspend fun create(userDto: UserDto) {
        userDBI.create(userDto)
        userCM.cache(userDto)
    }

    suspend fun get(id: IxId<UserDto>) : UserDto? {
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
    suspend fun getFromEmail(email: String) : UserDto? {
        return userDBI.get(email)
    }

    suspend fun verifyEmail(id: IxId<UserDto>) {
        userDBI.verifyEmail(id)
        userCM.delete(id)
    }

    suspend fun resetPassword(id: IxId<UserDto>, newPasswordHashed: String, verifyEmail: Boolean) {
        userDBI.resetPassword(id, newPasswordHashed, verifyEmail)
        userCM.delete(id)
    }

    suspend fun delete(id: IxId<UserDto>) {
        userCM.delete(id)
        userDBI.delete(id)

        listCM.deleteAll(id)
        categoryCM.deleteAllOfUser(id)
        itemCM.deleteAllOfUser(id)
        itemContentCM.deleteAllOfUser(id)
        taskCM.deleteAll(id)
    }
}
