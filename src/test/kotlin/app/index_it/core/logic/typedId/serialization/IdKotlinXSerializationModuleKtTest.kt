package app.index_it.core.logic.typedId.serialization

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.core.logic.typedId.newIxId
import app.index_it.core.logic.typedId.newIxIntId
import app.index_it.data.models.email.EmailVerificationDto
import app.index_it.data.models.user.UserDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IdKotlinXSerializationModuleKtTest {
    private val json = Json {
        serializersModule = IdKotlinXSerializationModule
        prettyPrint = true
    }

    @Test
    fun idSerialization() {
        val id = newIxId<UserDto>()
        val serialized = json.encodeToString(id).let {
            it.substring(1, it.length - 1)
        }

        assertEquals(id.toString(), serialized)

        val intId = newIxIntId<EmailVerificationDto>()
        val serializedInt = json.encodeToString(intId).let {
            it.substring(1, it.length - 1)
        }

        assertEquals(intId.toString(), serializedInt)
    }

    @Test
    fun objectWithIdSerialization() {

        @Serializable
        data class TestObject(
            @Contextual val id: IxId<UserDto>,
            val name: String
        )

        val testObject = TestObject(
            id = newIxId(),
            name = "test"
        )
        val serialized = json.encodeToString(testObject)

        assertEquals("""
            {
                "id": "${testObject.id}",
                "name": "${testObject.name}"
            }
        """.trimIndent(), serialized)
    }
}