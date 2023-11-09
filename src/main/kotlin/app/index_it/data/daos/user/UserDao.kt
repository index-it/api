package app.index_it.data.daos.user

import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.cache.cm.users.UserCM
import app.index_it.data.sources.mongo.users.UserDBM
import org.litote.kmongo.Id

object UserDao {
    /*
    fun exists(id: Id<UserDto>): Boolean = UserDBM.exists(id)
    fun existsWithEmail(email: String): Boolean = UserDBM.existsWithEmail(email)
     */

    fun create(userDto: UserDto) {
        UserDBM.create(userDto)
        UserCM.cache(userDto)
    }

    fun get(id: Id<UserDto>) : UserDto? {
        var user = UserCM.get(id)

        if (user == null) {
            user = UserDBM.get(id) ?: return null
            UserCM.cache(user)
        }

        return user
    }

    /**
     * This method should be only used in the login route
     */
    fun getFromEmail(email: String) : UserDto? {
        return UserDBM.getFromEmail(email)
    }

    fun verifyEmail(id: Id<UserDto>): UserDto? {
        return UserDBM.verifyEmail(id)?.let {
            UserCM.cache(it)
            it
        } ?: run {
            UserCM.delete(id)
            null
        }
    }

    fun resetPassword(id: Id<UserDto>, newPasswordHashed: String, verifyEmail: Boolean): UserDto? {
        return UserDBM.resetPassword(id, newPasswordHashed, verifyEmail)?.let {
            UserCM.cache(it)
            it
        } ?: run {
            UserCM.delete(id)
            null
        }
    }

    fun delete(id: Id<UserDto>) {
        UserCM.delete(id)
        UserDBM.delete(id)
    }
}
