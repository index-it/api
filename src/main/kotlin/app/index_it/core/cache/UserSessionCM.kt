package app.index_it.core.cache

import app.index_it.models.auth.UserSessionDto
import app.index_it.models.user.UserDto
import org.litote.kmongo.Id

/*
 * sessions-12345 (12345 is the user id)
 *     |--> { id: 486575, iat: 8878665574584 } (486575 is the session id)
 *     |--> { id: 234543, iat: 2435465436546 }
 * sessions-53211
 *     |--> { id: 32432, iat: 23453434545454 }
 *     |--> { id: 12453, iat: 25743563453444 }
 */

object UserSessionCM : DoubleHashedCM("sessions") {
    fun get(userId: Id<UserDto>, sessionId: String) : UserSessionDto? = getValue(userId.toString(), sessionId)

    fun create(userId: Id<UserDto>, userSessionDto: UserSessionDto) = cacheValue(userId.toString(), userSessionDto.id, userSessionDto)

    fun delete(userId: Id<UserDto>, sessionId: String) = uncacheValue(userId.toString(), sessionId)

    fun deleteAll(userId: Id<UserDto>) = uncacheAllValues(userId.toString())
}
