package app.index_it.daos

import app.index_it.core.cache.UserSessionCM
import app.index_it.models.user.UserSessionDto
import app.index_it.plugins.UserSessionId
import org.litote.kmongo.Id

object UserSessionDao {
    fun get(id: String) = UserSessionCM.get(id)

    fun create(userSessionDto: UserSessionDto) = UserSessionCM.create(userSessionDto)

    fun delete(id: String) = UserSessionCM.delete(id)
}
