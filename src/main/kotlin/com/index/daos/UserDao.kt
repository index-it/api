package com.index.daos

import com.index.core.clients.MongoClient
import com.index.core.clients.RedisClient
import com.index.models.user.UserDto
import com.index.models.user.UserLoginDto

object UserDao {
    fun getUser(id: String) : UserDto? {
        var user = RedisClient.getUserOrNull(id)

        if (user == null) {
            user = MongoClient.getUserOrNull(id) ?: return null
            RedisClient.cacheUser(user)
        }

        return user
    }

    fun getUserLogin(email: String) : UserLoginDto? {
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
}
