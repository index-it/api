package app.index.data.sources.cache.cm.users

import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserData

interface UserCM {
    fun cache(userData: UserData)

    fun get(id: IxId<UserData>): UserData?

    fun delete(id: IxId<UserData>)
}
