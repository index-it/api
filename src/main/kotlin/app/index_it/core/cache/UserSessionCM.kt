package app.index_it.core.cache

import app.index_it.models.user.UserSessionDto

object UserSessionCM : HashedCM("sessions") {
    fun get(id: String) : UserSessionDto? = getValue(id)

    fun create(userSessionDto: UserSessionDto) = cacheValue(userSessionDto.id, userSessionDto)

    fun delete(id: String) = uncacheValue(id)
}
