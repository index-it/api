package com.index.daos

import com.index.core.cache.UserSessionCM

object SessionDao {
    fun get(id: String) = UserSessionCM.getSession(id)


}
