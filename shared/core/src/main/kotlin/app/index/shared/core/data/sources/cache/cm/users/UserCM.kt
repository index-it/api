package app.index.shared.core.data.sources.cache.cm.users

import app.index.shared.core.typedId.impl.IxId
import app.index.shared.core.data.models.user.UserData

interface UserCM {
    fun cache(userData: UserData)

    fun get(id: IxId<UserData>): UserData?

    fun delete(id: IxId<UserData>)
}
