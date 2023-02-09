package app.index_it.daos

import app.index_it.core.cache.UserCM
import app.index_it.core.db.UserDBM
import app.index_it.models.user.ClientUserDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

object UserDao {
    fun exists(email: String): Boolean = UserDBM.exists(email)

    fun create(userDto: UserDto) {
        UserDBM.create(userDto)
        UserCM.create(userDto)
    }

    fun get(id: Id<UserDto>) : UserDto? {
        var user = UserCM.get(id)

        if (user == null) {
            user = UserDBM.get(id) ?: return null
            UserCM
        }

        return user
    }

    /**
     * This method should be only used in the login route
     */
    fun getFromEmail(email: String) : UserDto? {
        return UserDBM.getFromEmail(email)
    }

    fun update(id: Id<UserDto>, clientUserDto: ClientUserDto): UserDto? {
        return UserDBM.update(id, clientUserDto)?.also {
            UserCM.create(it)
        } ?: run {
            UserCM.delete(id)
            null
        }
    }

    fun verifyEmail(id: Id<UserDto>): UserDto? {
        return UserDBM.verifyEmail(id)?.let {
            UserCM.create(it)
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
