package app.index_it.core.cache

import app.index_it.models.user.UserDto

object UserCM : HashedCM("users") {
    fun getUser(id: String) : UserDto? = getValue(id)
}
