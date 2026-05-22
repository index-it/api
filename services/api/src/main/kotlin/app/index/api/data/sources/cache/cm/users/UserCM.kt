package app.index.api.data.sources.cache.cm.users

import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.data.models.user.UserData

interface UserCM {
    fun cache(userData: UserData)

    fun get(id: IxId<UserData>): UserData?

    fun delete(id: IxId<UserData>)
}
