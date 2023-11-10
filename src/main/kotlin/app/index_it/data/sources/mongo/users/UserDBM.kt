package app.index_it.data.sources.mongo.users

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.mongo.MongoClient
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*

object UserDBM {
    private val col = MongoClient.database.getCollection<UserDto>("users")

    init {
        col.ensureUniqueIndex(UserDto::email)
    }

    /*
    fun exists(id: IxId<UserDto>): Boolean {
        return col.findOne(UserDto::id eq id) != null
    }
     */

    /*
    fun existsWithEmail(email: String): Boolean {
        return col.findOne(UserDto::email eq email) != null
    }
     */

    fun create(userDto: UserDto) {
        col.save(userDto)
    }

    fun get(id: IxId<UserDto>): UserDto? {
        return col.findOne(UserDto::id eq id)
    }

    fun getFromEmail(email: String): UserDto? {
        return col.findOne(UserDto::email eq email)
    }

    fun verifyEmail(id: IxId<UserDto>): UserDto? {
        return col.findOneAndUpdate(
            UserDto::id eq id,
            setValue(UserDto::emailVerified, true),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun resetPassword(id: IxId<UserDto>, newPasswordHashed: String, verifyEmail: Boolean): UserDto? {
        return if (verifyEmail) col.findOneAndUpdate(
            UserDto::id eq id,
            set(
                UserDto::passwordHash setTo newPasswordHashed,
                UserDto::emailVerified setTo true
            ),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        ) else col.findOneAndUpdate(
            UserDto::id eq id,
            setValue(UserDto::passwordHash, newPasswordHashed),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun delete(id: IxId<UserDto>) {
        col.deleteOne(UserDto::id eq id)
    }
}
