package app.index_it.daos

import app.index_it.core.cache.UserCM
import app.index_it.core.db.UserDBM
import app.index_it.models.user.UserDto

object UserDao {
    fun getUser(id: String) : UserDto? {
        var user = UserCM.getUser(id)

        if (user == null) {
            user = UserDBM.getUser(id) ?: return null
            // UserCM.cacheUser(user)
        }

        return user
    }

    /*fun getUserLogin(email: String) : UserLoginDto? {
        var userLogin = RedisClient.getUserEmail(email)

        if (userLogin == null) {
            val user = MongoClient.getUserLogin(email) ?: return null
            userLogin = UserLoginDto(
                user.email,
                user.password_hash
            )

            RedisClient.cacheUserLogin(userLogin)
        }

        return userLogin
    }
    
     */
}
