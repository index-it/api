package app.index_it.data.daos.user

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.lists.CategoryCM
import app.index_it.data.sources.cache.cm.lists.ItemCM
import app.index_it.data.sources.cache.cm.lists.ItemContentCM
import app.index_it.data.sources.cache.cm.lists.ListCM
import app.index_it.data.sources.cache.cm.tasks.TaskCM
import app.index_it.data.sources.cache.cm.users.UserCM
import app.index_it.data.sources.db.dbi.user.impl.UserDBIImpl

object UserDao {
    /*
    fun exists(id: IxId<UserDto>): Boolean = UserDBIImpl.exists(id)
    fun existsWithEmail(email: String): Boolean = UserDBIImpl.existsWithEmail(email)
     */

    suspend fun create(userDto: UserDto) {
        UserDBIImpl.create(userDto)
        UserCM.cache(userDto)
    }

    suspend fun get(id: IxId<UserDto>) : UserDto? {
        var user = UserCM.get(id)

        if (user == null) {
            user = UserDBIImpl.get(id) ?: return null
            UserCM.cache(user)
        }

        return user
    }

    /**
     * This method should be only used in the login route
     */
    suspend fun getFromEmail(email: String) : UserDto? {
        return UserDBIImpl.get(email)
    }

    suspend fun verifyEmail(id: IxId<UserDto>) {
        UserDBIImpl.verifyEmail(id)
        UserCM.delete(id)
    }

    suspend fun resetPassword(id: IxId<UserDto>, newPasswordHashed: String, verifyEmail: Boolean) {
        UserDBIImpl.resetPassword(id, newPasswordHashed, verifyEmail)
        UserCM.delete(id)
    }

    suspend fun delete(id: IxId<UserDto>) {
        UserCM.delete(id)
        UserDBIImpl.delete(id)

        ListCM.deleteAll(id)
        CategoryCM.deleteAllOfUser(id)
        ItemCM.deleteAllOfUser(id)
        ItemContentCM.deleteAllOfUser(id)
        TaskCM.deleteAll(id)
    }
}
