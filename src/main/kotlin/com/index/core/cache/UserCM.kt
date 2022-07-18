package com.index.core.cache

import com.index.models.user.UserDto

object UserCM : HashedCM("users") {
    fun getUser(id: String) : UserDto? = getValue(id)
}
