package app.index_it.daos

import app.index_it.core.cache.UserSessionCM

object SessionDao {
    fun get(id: String) = UserSessionCM.getSession(id)


}
